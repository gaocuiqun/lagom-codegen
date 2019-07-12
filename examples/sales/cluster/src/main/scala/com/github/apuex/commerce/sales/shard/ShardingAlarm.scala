/*****************************************************
 ** This file is 100% ***GENERATED***, DO NOT EDIT! **
 *****************************************************/
package com.github.apuex.commerce.sales.shard

import akka.actor._
import akka.cluster.sharding._
import com.github.apuex.commerce.sales._
import com.github.apuex.commerce.sales.domain._
import scala.collection.convert.ImplicitConversions._

import scala.math.Numeric.IntIsIntegral._

object ShardingAlarm {
  def props = Props[ShardingAlarm]
  def name: String = "ShardingAlarm"

  val shardName: String = "ShardingAlarm"
  var defaultNumberOfShards = 100

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Command =>
      (cmd.entityId.toString, cmd)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case cmd: Command =>
      (abs(cmd.entityId.hashCode) % defaultNumberOfShards).toString
  }
}

class ShardingAlarm () extends AlarmActor () {
  override def unhandled(message: Any): Unit = message match {
    case x => log.info("unhandled COMMAND: {} {}", this, x)
  }
}
