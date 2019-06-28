/*****************************************************
 ** This file is 100% ***GENERATED***, DO NOT EDIT! **
 *****************************************************/
package com.github.apuex.commerce.sales.dao

import com.github.apuex.commerce.sales._
import com.github.apuex.springbootsolution.runtime._
import com.google.protobuf.timestamp.Timestamp
import java.sql.Connection

trait OrderItemDao {
  def createOrderItem(cmd: CreateOrderItemCmd)(implicit conn: Connection): Int

  def retrieveOrderItem(cmd: RetrieveOrderItemCmd)(implicit conn: Connection): OrderItemVo

  def updateOrderItem(cmd: UpdateOrderItemCmd)(implicit conn: Connection): Int

  def deleteOrderItem(cmd: DeleteOrderItemCmd)(implicit conn: Connection): Int

  def queryOrderItem(cmd: QueryCommand)(implicit conn: Connection): Seq[OrderItemVo]

  def retrieveOrderItemByRowid(cmd: RetrieveByRowidCmd)(implicit conn: Connection): OrderItemVo

  def selectByOrderId(orderId: String)(implicit conn: Connection): Seq[OrderItemVo]

  def deleteByOrderId(orderId: String)(implicit conn: Connection): Int

  def selectByProductId(productId: String)(implicit conn: Connection): Seq[OrderItemVo]

  def deleteByProductId(productId: String)(implicit conn: Connection): Int
}
