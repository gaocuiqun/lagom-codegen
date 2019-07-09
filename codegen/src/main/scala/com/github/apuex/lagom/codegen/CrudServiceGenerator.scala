package com.github.apuex.lagom.codegen

import com.github.apuex.lagom.codegen.ModelLoader._
import com.github.apuex.springbootsolution.runtime.SymbolConverters._
import com.github.apuex.springbootsolution.runtime.TextUtils.indent
import com.github.apuex.springbootsolution.runtime.TypeConverters.toJavaType

import scala.xml.Node

object CrudServiceGenerator {
  def apply(fileName: String): CrudServiceGenerator = CrudServiceGenerator(ModelLoader(fileName))

  def apply(modelLoader: ModelLoader): CrudServiceGenerator = new CrudServiceGenerator(modelLoader)
}

class CrudServiceGenerator(modelLoader: ModelLoader) {

  import modelLoader._

  def generate(): Unit = {
    val content = generateServiceImpl()
    save(
      s"${cToPascal(s"${modelName}_${service}_${impl}")}.scala",
      content,
      crudImplSrcDir
    )
    save(
      s"${cToPascal(s"${modelName}_${service}_${impl}")}.scala",
      content,
      implSrcDir
    )
  }

  def generateServiceImpl(): String = {
    val constructorParams = (
      xml.child.filter(_.label == "entity").map(_.\@("name"))
        .map(x => s"${cToCamel(x)}Dao: ${cToPascal(x)}Dao") ++
        Seq(
          s"eventApply: ${cToPascal(s"${modelName}_${event}_${apply}")}",
          "publishQueue: String",
          "mediator: ActorRef",
          "duration: FiniteDuration",
          "db: Database"
        )
      )
      .reduceOption((l, r) => s"${l},\n${r}")
      .getOrElse("")

    s"""
       |/*****************************************************
       | ** This file is 100% ***GENERATED***, DO NOT EDIT! **
       | *****************************************************/
       |package ${crudImplSrcPackage}
       |
       |import java.util.Date
       |
       |import akka._
       |import akka.actor._
       |import akka.cluster.pubsub.DistributedPubSubMediator._
       |import akka.stream.scaladsl._
       |import akka.stream.{OverflowStrategy, SourceShape}
       |import ${messageSrcPackage}.ScalapbJson._
       |import ${messageSrcPackage}._
       |import ${messageSrcPackage}.dao._
       |import com.github.apuex.events.play.EventEnvelope
       |import com.github.apuex.springbootsolution.runtime.DateFormat._
       |import com.github.apuex.springbootsolution.runtime.FilterPredicate.Clause.{Connection, Predicate}
       |import com.github.apuex.springbootsolution.runtime.LogicalConnectionType.AND
       |import com.github.apuex.springbootsolution.runtime._
       |import com.google.protobuf.any.Any
       |import com.google.protobuf.timestamp.Timestamp
       |import com.lightbend.lagom.scaladsl.api._
       |import play.api.db.Database
       |import scalapb.GeneratedMessage
       |
       |import scala.concurrent.Future
       |import scala.concurrent.duration.FiniteDuration
       |
       |class ${cToPascal(modelName)}ServiceImpl (${indent(constructorParams, 2)})
       |  extends ${cToPascal(modelName)}Service {
       |
       |  ${indent(calls(), 2)}
       |
       |  ${indent(defCurrentEvents(), 2)}
       |
       |  ${indent(defEvents(), 2)}
       |
       |  ${indent(defQueryForEvents(), 2)}
       |}
     """.stripMargin.trim
  }

  def calls(): String = calls(xml)
    .reduceOption((l, r) => s"${l}\n\n${r}")
    .getOrElse("")

  def calls(root: Node): Seq[String] = {
    root.child.filter(_.label == "entity")
      .map(x => {
        val aggregatesTo = x.\@("aggregatesTo")
        val enum = if ("true" == x.\@("enum")) true else false
        if (!enum && "" == aggregatesTo) generateCallsForAggregate(toAggregate(x, root))
        else {
          val valueObject = toValueObject(x, aggregatesTo, root)
          generateCallsForValueObject(valueObject)
        }
      })
  }

  def defCallsForEmbeddedAggregateMessage(name: String, aggregate: Aggregate): String = {
    val nonKeyFieldCount = aggregate.fields.length - aggregate.primaryKey.fields.length
    val keyFieldNames = aggregate.primaryKey.fields.map(_.name).toSet
    val nonKeyFields = aggregate.fields.filter(x => !keyFieldNames.contains(x.name))
    val get =
      s"""
         |def get${cToPascal(aggregate.name)}(): ServiceCall[Get${cToPascal(aggregate.name)}Cmd, ${cToPascal(aggregate.name)}Vo] = ServiceCall { cmd =>
         |  Future.successful(
         |    db.withTransaction { implicit c =>
         |       ${cToCamel(name)}Dao.get${cToPascal(aggregate.name)}(cmd)
         |    }
         |  )
         |}
     """.stripMargin.trim
    val update = if (nonKeyFieldCount > 1)
      s"""
         |def update${cToPascal(aggregate.name)}(): ServiceCall[Update${cToPascal(aggregate.name)}Cmd, Int] = ServiceCall { cmd =>
         |  Future.successful(
         |    db.withTransaction { implicit c =>
         |      val evt = Update${cToPascal(aggregate.name)}Event(${substituteMethodParams(userField +: aggregate.fields, "cmd")})
         |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
         |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
         |      )
         |      mediator ! Publish(publishQueue, evt)
         |      ${cToCamel(name)}Dao.update${cToPascal(aggregate.name)}(evt)
         |    }
         |  )
         |}
     """.stripMargin.trim
    else if (nonKeyFieldCount == 1) {
      val field = nonKeyFields.head
      if ("array" == field._type || "map" == field._type)
        s"""
           |def add${cToPascal(aggregate.name)}(): ServiceCall[Add${cToPascal(aggregate.name)}Cmd, Int] = ServiceCall { cmd =>
           |  Future.successful(
           |    db.withTransaction { implicit c =>
           |      val evt = Add${cToPascal(aggregate.name)}Event(${substituteMethodParams(userField +: aggregate.fields, "cmd")})
           |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
           |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
           |      )
           |      mediator ! Publish(publishQueue, evt)
           |      ${cToCamel(name)}Dao.add${cToPascal(aggregate.name)}(evt)
           |    }
           |  )
           |}
           |
           |def remove${cToPascal(aggregate.name)}(): ServiceCall[Remove${cToPascal(aggregate.name)}Cmd, Int] = ServiceCall { cmd =>
           |  Future.successful(
           |    db.withTransaction { implicit c =>
           |      val evt = Remove${cToPascal(aggregate.name)}Event(${substituteMethodParams(userField +: aggregate.primaryKey.fields, "cmd")})
           |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
           |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
           |      )
           |      mediator ! Publish(publishQueue, evt)
           |      ${cToCamel(name)}Dao.remove${cToPascal(aggregate.name)}(evt)
           |    }
           |  )
           |}
     """.stripMargin.trim
      else
        s"""
           |def change${cToPascal(aggregate.name)}(): ServiceCall[Change${cToPascal(aggregate.name)}Cmd, Int] = ServiceCall { cmd =>
           |  Future.successful(
           |    db.withTransaction { implicit c =>
           |      val evt = Change${cToPascal(aggregate.name)}Event(${substituteMethodParams(userField +: aggregate.fields, "cmd")})
           |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
           |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
           |      )
           |      mediator ! Publish(publishQueue, evt)
           |      ${cToCamel(name)}Dao.change${cToPascal(aggregate.name)}(evt)
           |    }
           |  )
           |}
     """.stripMargin.trim
    } else { // this cannot be happen.
      s"""
         |
     """.stripMargin.trim
    }
    s"""
       |${get}
       |
       |${update}
     """.stripMargin.trim
  }

  def defCallsForEmbeddedAggregateMessages(name: String, aggregates: Seq[Aggregate]): Seq[String] = {
    aggregates.map(defCallsForEmbeddedAggregateMessage(name, _))
  }

  def generateCallsForAggregate(aggregate: Aggregate): String = {
    import aggregate._
    (
      defCrudCalls(name, fields, primaryKey) ++
        defByForeignKeyCalls(name, fields, foreignKeys) ++
        defMessageCalls(aggregate.messages, name, fields, primaryKey) ++
        defCallsForEmbeddedAggregateMessages(aggregate.name, aggregate.aggregates)
      )
      .reduceOption((l, r) => s"${l}\n\n${r}")
      .getOrElse("")
  }

  def generateCallsForValueObject(valueObject: ValueObject): String = {
    import valueObject._
    (
      defCrudCalls(name, fields, primaryKey) ++
        defByForeignKeyCalls(name, fields, foreignKeys)
      )
      .reduceOption((l, r) => s"${l}\n\n${r}")
      .getOrElse("")
  }

  def defMessageCall(message: Message, parentName: String, parentFields: Seq[Field], primaryKey: PrimaryKey): String = {
    val key = primaryKey.fields.map(_.name).toSet
    val derived = parentFields.map(_.name).filter(!key.contains(_)).toSet
    val multiple = message.returnType.endsWith("*")
    val returnType = if ("" == message.returnType) "Int"
    else {
      val baseName = message.returnType.replace("*", "")
      if (multiple) {
        if (isAggregateEntity(baseName)) s"${cToPascal(baseName)}ListVo]" else s"${cToPascal(baseName)}Vo"
      } else {
        cToPascal(toJavaType(baseName))
      }
    }

    val daoCall = if (message.transient)
      s"""
         |mediator ! Publish(publishQueue, cmd)
         |0
       """.stripMargin.trim
    else if (message.fields.filter(x => derived.contains(x.name)).isEmpty)
      s"""
         |val evt = ${cToPascal(message.name)}Event(${substituteMethodParams(userField +: message.fields, "cmd")})
         |mediator ! Publish(publishQueue, evt)
         |${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
         |  Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
         |)
       """.stripMargin.trim
    else if (multiple)
      s"""
         |val evt = ${cToPascal(message.name)}Event(${substituteMethodParams(userField +: message.fields, "cmd")})
         |${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
         |  Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
         |)
         |mediator ! Publish(publishQueue, evt)
         |${returnType}(
         |  ${cToCamel(parentName)}Dao.${cToCamel(message.name)}(evt)
         |)
       """.stripMargin.trim
    else
      s"""
         |val evt = ${cToPascal(message.name)}Event(${substituteMethodParams(userField +: message.fields, "cmd")})
         |${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
         |  Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
         |)
         |mediator ! Publish(publishQueue, evt)
         |${cToCamel(parentName)}Dao.${cToCamel(message.name)}(evt)
       """.stripMargin.trim

    s"""
       |def ${cToCamel(message.name)}(): ServiceCall[${cToPascal(message.name)}Cmd, ${returnType}] = ServiceCall { cmd =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |      ${indent(daoCall, 6)}
       |    }
       |  )
       |}
     """.stripMargin.trim
  }

  def defMessageCalls(messages: Seq[Message], parentName: String, parentFields: Seq[Field], primaryKey: PrimaryKey): Seq[String] = {
    messages.map(defMessageCall(_, parentName, parentFields, primaryKey))
  }

  def defCrudCalls(name: String, fields: Seq[Field], primaryKey: PrimaryKey): Seq[String] = Seq(
    s"""
       |def create${cToPascal(name)}(): ServiceCall[Create${cToPascal(name)}Cmd, Int] = ServiceCall { cmd =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |      val evt = Create${cToPascal(name)}Event(${substituteMethodParams(userField +: fields, "cmd")})
       |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
       |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
       |      )
       |      mediator ! Publish(publishQueue, evt)
       |      ${cToCamel(name)}Dao.create${cToPascal(name)}(evt)
       |    }
       |  )
       |}
     """.stripMargin.trim,
    s"""
       |def retrieve${cToPascal(name)}(): ServiceCall[Retrieve${cToPascal(name)}Cmd, ${cToPascal(name)}Vo] = ServiceCall { cmd =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |      ${cToCamel(name)}Dao.retrieve${cToPascal(name)}(cmd)
       |    }
       |  )
       |}
     """.stripMargin.trim,
    s"""
       |def update${cToPascal(name)}(): ServiceCall[Update${cToPascal(name)}Cmd, Int] = ServiceCall { cmd =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |      val evt = Update${cToPascal(name)}Event(${substituteMethodParams(userField +: fields, "cmd")})
       |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
       |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
       |      )
       |      mediator ! Publish(publishQueue, evt)
       |      ${cToCamel(name)}Dao.update${cToPascal(name)}(evt)
       |    }
       |  )
       |}
     """.stripMargin.trim,
    s"""
       |def delete${cToPascal(name)}(): ServiceCall[Delete${cToPascal(name)}Cmd, Int] = ServiceCall { cmd =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |      val evt = Delete${cToPascal(name)}Event(${substituteMethodParams(userField +: primaryKey.fields, "cmd")})
       |      ${cToCamel(journalTable)}Dao.create${cToPascal(journalTable)}(
       |        Create${cToPascal(journalTable)}Event(cmd.userId, 0L, cmd.entityId, Some(toScalapbTimestamp(new Date())), evt.getClass.getName, evt.toByteString)
       |      )
       |      mediator ! Publish(publishQueue, evt)
       |      ${cToCamel(name)}Dao.delete${cToPascal(name)}(evt)
       |    }
       |  )
       |}
     """.stripMargin.trim,
    s"""
       |def query${cToPascal(name)}(): ServiceCall[QueryCommand, ${cToPascal(name)}ListVo] = ServiceCall { cmd =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |       ${cToPascal(name)}ListVo(${cToCamel(name)}Dao.query${cToPascal(name)}(cmd))
       |    }
       |  )
       |}
     """.stripMargin.trim,
    s"""
       |def retrieve${cToPascal(name)}ByRowid(rowid: String): ServiceCall[NotUsed, ${cToPascal(name)}Vo] = ServiceCall { _ =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |       ${cToCamel(name)}Dao.retrieve${cToPascal(name)}ByRowid(rowid)
       |    }
       |  )
       |}
     """.stripMargin.trim
  )

  def defByForeignKeyCalls(name: String, fields: Seq[Field], foreignKeys: Seq[ForeignKey]): Seq[String] = {
    foreignKeys
      .map(x => {
        val fieldNames = x.fields
          .map(_.name)
          .toSet

        val fkFields = fields
          .filter(x => fieldNames.contains(x.name))
        defByForeignKeyCall(name, fkFields)
      })
  }

  def defByForeignKeyCall(name: String, keyFields: Seq[Field]): String = {
    val by = keyFields
      .map(x => cToPascal(x.name))
      .reduceOption((x, y) => s"${x}${y}")
      .getOrElse("")

    s"""
       |def select${cToPascal(name)}By${by}(${defMethodParams(keyFields)}): ServiceCall[NotUsed, ${cToPascal(name)}ListVo] = ServiceCall { _ =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |       ${cToPascal(name)}ListVo(${cToCamel(name)}Dao.selectBy${by}(${substituteMethodParams(keyFields)}))
       |    }
       |  )
       |}
       |
       |def delete${cToPascal(name)}By${by}(${defMethodParams(keyFields)}): ServiceCall[NotUsed, Int] = ServiceCall { _ =>
       |  Future.successful(
       |    db.withTransaction { implicit c =>
       |       ${cToCamel(name)}Dao.deleteBy${by}(${substituteMethodParams(keyFields)})
       |    }
       |  )
       |}
     """.stripMargin.trim
  }

  def defCurrentEvents(): String = {
    s"""
       |def currentEvents(): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]] = {
       |  ServiceCall { is =>
       |    Future.successful({
       |      val replySource = is
       |        .map(parseJson)
       |        .map(eventApply.on)
       |        .filter(_ => false) // to drainage
       |        .map(x => printer.print(x.asInstanceOf[GeneratedMessage]))
       |
       |      val commandSource: Source[String, ActorRef] = Source.actorRef[scala.Any](
       |        512,
       |        OverflowStrategy.dropHead)
       |        .filter(x => x.isInstanceOf[Event])
       |        .map({
       |          case x: Event =>
       |            EventEnvelope(
       |              "",          // offset
       |              x.entityId,  // persistence_id
       |              0L,          // sequence_nr
       |              Some(Any.of(s"type.googleapis.com/$${x.getClass.getName}", x.asInstanceOf[GeneratedMessage].toByteString)))
       |        })
       |        .map(printer.print(_))
       |
       |      Source.fromGraph(GraphDSL.create() { implicit builder =>
       |        import akka.stream.scaladsl.GraphDSL.Implicits._
       |        val replyShape = builder.add(replySource)
       |        val materializedCommandSource = commandSource.mapMaterializedValue(actorRef => mediator ! Subscribe(publishQueue, actorRef))
       |        val commandShape = builder.add(materializedCommandSource)
       |
       |        val merge = builder.add(Merge[String](2))
       |
       |        replyShape ~> merge
       |        commandShape ~> merge
       |
       |        SourceShape(merge.out)
       |      })
       |    })
       |  }
       |}
     """.stripMargin.trim
  }

  def defEvents(): String = {
    s"""
      |def events(offset: Option[String]): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]] = {
      |  ServiceCall { is =>
      |    Future.successful({
      |      // reply/confirm to inbound message...
      |      val replySource = is
      |        .map(parseJson)
      |        .map(eventApply.on)
      |        .filter(_ => false) // to drainage
      |        .map(x => printer.print(x.asInstanceOf[GeneratedMessage]))
      |
      |      val commandSource: Source[String, ActorRef] = Source.actorRef[scala.Any](
      |        512,
      |        OverflowStrategy.dropHead)
      |        .filter(x => x.isInstanceOf[Command])
      |        .map({
      |          case x: Command =>
      |            EventEnvelope(
      |              "",          // offset
      |              x.entityId,  // persistence_id
      |              0L,          // sequence_nr
      |              Some(Any.of(s"type.googleapis.com/$${x.getClass.getName}", x.asInstanceOf[GeneratedMessage].toByteString)))
      |        })
      |        .map(printer.print(_))
      |
      |      val eventSource = Source.fromIterator(() => new Iterator[Seq[${cToPascal(journalTable)}Vo]] {
      |        var lastOffset: Option[String] = Some(offset.getOrElse("0"))
      |
      |        override def hasNext: Boolean = true
      |
      |        override def next(): Seq[${cToPascal(journalTable)}Vo] = db.withTransaction { implicit c =>
      |          val result = ${cToCamel(journalTable)}Dao.query${cToPascal(journalTable)}(queryForEventsCmd(lastOffset).withOrderBy(Seq(OrderBy("offset", OrderType.ASC))))
      |          if (!result.isEmpty) {
      |            lastOffset = Some(result.last.offset.toString)
      |          }
      |          result
      |        }
      |      })
      |        .throttle(1, duration)
      |        .flatMapMerge(2, x => Source(x.toList))
      |        .map(x => EventEnvelope(
      |          x.offset.toString,
      |          x.persistenceId,
      |          0,
      |          Some(Any.of(s"type.googleapis.com/$${x.metaData}", x.content)))
      |        )
      |        .map(printer.print(_))
      |
      |      Source.fromGraph(GraphDSL.create() { implicit builder =>
      |        import akka.stream.scaladsl.GraphDSL.Implicits._
      |        val replyShape = builder.add(replySource)
      |        val eventShape = builder.add(eventSource)
      |        val materializedCommandSource = commandSource.mapMaterializedValue(actorRef => mediator ! Subscribe(publishQueue, actorRef))
      |        val commandShape = builder.add(materializedCommandSource)
      |
      |        val merge = builder.add(Merge[String](3))
      |
      |        replyShape ~> merge
      |        eventShape ~> merge
      |        commandShape ~> merge
      |
      |        SourceShape(merge.out)
      |      })
      |    })
      |  }
      |}
    """.stripMargin.trim
  }

  def defQueryForEvents(): String = {
    s"""
       |private def queryForEventsCmd(offset: Option[String]): QueryCommand = {
       |  QueryCommand(
       |    Some(
       |      FilterPredicate(
       |        Connection(
       |          LogicalConnectionVo(
       |            AND,
       |            Seq(
       |              FilterPredicate(
       |                Predicate(
       |                  LogicalPredicateVo(
       |                    PredicateType.GT,
       |                    "offset",
       |                    Seq("offset")
       |                  )
       |                )
       |              )
       |            )
       |          )
       |        )
       |      )
       |    ),
       |    Map(
       |      "offset" -> offset.getOrElse("")
       |    )
       |  )
       |}
     """.stripMargin.trim
  }
}


