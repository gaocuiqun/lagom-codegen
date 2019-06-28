package com.github.apuex.lagom.codegen

import com.github.apuex.lagom.codegen.ModelLoader._
import com.github.apuex.springbootsolution.runtime.SymbolConverters._
import com.github.apuex.springbootsolution.runtime.TextUtils.indent


object ServiceGenerator {
  def apply(fileName: String): ServiceGenerator = new ServiceGenerator(ModelLoader(fileName))

  def apply(modelLoader: ModelLoader): ServiceGenerator = new ServiceGenerator(modelLoader)


  def retrieveByRowid(name: String): String = {
    s"""
       |def retrieve${cToPascal(name)}ByRowid(): ServiceCall[RetrieveByRowidCmd, ${cToPascal(name)}Vo]
     """.stripMargin.trim
  }

  def create(name: String): String = {
    s"""
       |def create${cToPascal(name)}(): ServiceCall[Create${cToPascal(name)}Cmd, NotUsed]
     """.stripMargin.trim
  }

  def retrieve(name: String): String = {
    s"""
       |def retrieve${cToPascal(name)}(): ServiceCall[Retrieve${cToPascal(name)}Cmd, ${cToPascal(name)}Vo]
     """.stripMargin.trim
  }

  def query(name: String): String = {
    s"""
       |def query${cToPascal(name)}(): ServiceCall[QueryCommand, ${cToPascal(name)}ListVo]
     """.stripMargin.trim
  }

  def update(name: String): String = {
    s"""
       |def update${cToPascal(name)}(): ServiceCall[Update${cToPascal(name)}Cmd, NotUsed]
     """.stripMargin.trim
  }

  def delete(name: String): String = {
    s"""
       |def delete${cToPascal(name)}(): ServiceCall[Delete${cToPascal(name)}Cmd, NotUsed]
     """.stripMargin.trim
  }
}

class ServiceGenerator(modelLoader: ModelLoader) {

  import modelLoader._

  def generate(): Unit = {
    save(
      s"${cToPascal(s"${modelName}_${service}")}.scala",
      generateService(),
      apiSrcDir
    )
  }

  def generateService(): String = {
    s"""
       |/*****************************************************
       | ** This file is 100% ***GENERATED***, DO NOT EDIT! **
       | *****************************************************/
       |package ${apiSrcPackage}
       |
       |import ${messageSrcPackage}._
       |import akka._
       |import akka.stream.scaladsl._
       |import com.lightbend.lagom.scaladsl.api._
       |import play.api.libs.json.Json
       |
       |trait ${cToPascal(modelName)}Service extends Service {
       |
       |  ${indent(calls(), 2)}
       |
       |  def events(offset: Option[String]): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]]
       |
       |  override def descriptor: Descriptor = {
       |    import Service._
       |    import ScalapbJson._
       |
       |    ${indent(callJsonFormats(), 4)}
       |
       |    named("${cToShell(modelName)}")
       |      .withCalls(
       |        ${indent(callDescs(), 8)}
       |        pathCall("/api/events?offset", events _)
       |      ).withAutoAcl(true)
       |  }
       |}
     """.stripMargin.trim
  }

  def calls(): String = ""
  def callJsonFormats(): String = ""
  def callDescs(): String = ""
}


