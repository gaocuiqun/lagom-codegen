/*****************************************************
 ** This file is 100% ***GENERATED***, DO NOT EDIT! **
 *****************************************************/
package com.github.apuex.commerce.sales.impl

import java.util.Date

import akka._
import akka.stream.scaladsl._
import com.github.apuex.commerce.sales._
import com.github.apuex.commerce.sales.dao._
import com.github.apuex.springbootsolution.runtime.DateFormat._
import com.github.apuex.springbootsolution.runtime._
import com.lightbend.lagom.scaladsl.api._
import play.api.db.Database

import scala.concurrent.Future

class SalesServiceImpl (alarmDao: AlarmDao,
  paymentTypeDao: PaymentTypeDao,
  productDao: ProductDao,
  orderDao: OrderDao,
  orderItemDao: OrderItemDao,
  eventJournalDao: EventJournalDao,
  db: Database)
  extends SalesService {

  def createAlarm(): ServiceCall[CreateAlarmCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = CreateAlarmEvent(cmd.userId, cmd.alarmId, cmd.alarmBegin, cmd.alarmEnd, cmd.alarmDesc)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        alarmDao.createAlarm(evt)
      }
    )
  }

  def retrieveAlarm(): ServiceCall[RetrieveAlarmCmd, AlarmVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        alarmDao.retrieveAlarm(cmd)
      }
    )
  }

  def updateAlarm(): ServiceCall[UpdateAlarmCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdateAlarmEvent(cmd.userId, cmd.alarmId, cmd.alarmBegin, cmd.alarmEnd, cmd.alarmDesc)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        alarmDao.updateAlarm(evt)
      }
    )
  }

  def deleteAlarm(): ServiceCall[DeleteAlarmCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = DeleteAlarmEvent(cmd.userId, cmd.alarmId, cmd.alarmBegin)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        alarmDao.deleteAlarm(evt)
      }
    )
  }

  def queryAlarm(): ServiceCall[QueryCommand, AlarmListVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         AlarmListVo(alarmDao.queryAlarm(cmd))
      }
    )
  }

  def retrieveAlarmByRowid(rowid: String): ServiceCall[NotUsed, AlarmVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         alarmDao.retrieveAlarmByRowid(rowid)
      }
    )
  }

  def beginAlarm(): ServiceCall[BeginAlarmCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = BeginAlarmEvent(cmd.userId, cmd.alarmId, cmd.alarmBegin, cmd.alarmDesc)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        alarmDao.beginAlarm(evt)
      }
    )
  }

  def endAlarm(): ServiceCall[EndAlarmCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = EndAlarmEvent(cmd.userId, cmd.alarmId, cmd.alarmBegin, cmd.alarmEnd, cmd.alarmDesc)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        alarmDao.endAlarm(evt)
      }
    )
  }

  def createPaymentType(): ServiceCall[CreatePaymentTypeCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = CreatePaymentTypeEvent(cmd.userId, cmd.paymentTypeId, cmd.paymentTypeName, cmd.paymentTypeLabel)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        paymentTypeDao.createPaymentType(evt)
      }
    )
  }

  def retrievePaymentType(): ServiceCall[RetrievePaymentTypeCmd, PaymentTypeVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        paymentTypeDao.retrievePaymentType(cmd)
      }
    )
  }

  def updatePaymentType(): ServiceCall[UpdatePaymentTypeCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdatePaymentTypeEvent(cmd.userId, cmd.paymentTypeId, cmd.paymentTypeName, cmd.paymentTypeLabel)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        paymentTypeDao.updatePaymentType(evt)
      }
    )
  }

  def deletePaymentType(): ServiceCall[DeletePaymentTypeCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = DeletePaymentTypeEvent(cmd.userId, cmd.paymentTypeId)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        paymentTypeDao.deletePaymentType(evt)
      }
    )
  }

  def queryPaymentType(): ServiceCall[QueryCommand, PaymentTypeListVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         PaymentTypeListVo(paymentTypeDao.queryPaymentType(cmd))
      }
    )
  }

  def retrievePaymentTypeByRowid(rowid: String): ServiceCall[NotUsed, PaymentTypeVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         paymentTypeDao.retrievePaymentTypeByRowid(rowid)
      }
    )
  }

  def createProduct(): ServiceCall[CreateProductCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = CreateProductEvent(cmd.userId, cmd.productId, cmd.productName, cmd.productUnit, cmd.unitPrice, cmd.recordTime, cmd.quantitySold)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.createProduct(evt)
      }
    )
  }

  def retrieveProduct(): ServiceCall[RetrieveProductCmd, ProductVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        productDao.retrieveProduct(cmd)
      }
    )
  }

  def updateProduct(): ServiceCall[UpdateProductCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdateProductEvent(cmd.userId, cmd.productId, cmd.productName, cmd.productUnit, cmd.unitPrice, cmd.recordTime, cmd.quantitySold)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.updateProduct(evt)
      }
    )
  }

  def deleteProduct(): ServiceCall[DeleteProductCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = DeleteProductEvent(cmd.userId, cmd.productId)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.deleteProduct(evt)
      }
    )
  }

  def queryProduct(): ServiceCall[QueryCommand, ProductListVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         ProductListVo(productDao.queryProduct(cmd))
      }
    )
  }

  def retrieveProductByRowid(rowid: String): ServiceCall[NotUsed, ProductVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         productDao.retrieveProductByRowid(rowid)
      }
    )
  }

  def getProductSales(): ServiceCall[GetProductSalesCmd, ProductSalesVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         productDao.getProductSales(cmd)
      }
    )
  }

  def updateProductSales(): ServiceCall[UpdateProductSalesCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdateProductSalesEvent(cmd.userId, cmd.productId, cmd.recordTime, cmd.quantitySold)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.updateProductSales(evt)
      }
    )
  }

  def getProductName(): ServiceCall[GetProductNameCmd, ProductNameVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         productDao.getProductName(cmd)
      }
    )
  }

  def changeProductName(): ServiceCall[ChangeProductNameCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = ChangeProductNameEvent(cmd.userId, cmd.productId, cmd.productName)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.changeProductName(evt)
      }
    )
  }

  def getProductUnit(): ServiceCall[GetProductUnitCmd, ProductUnitVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         productDao.getProductUnit(cmd)
      }
    )
  }

  def changeProductUnit(): ServiceCall[ChangeProductUnitCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = ChangeProductUnitEvent(cmd.userId, cmd.productId, cmd.productUnit)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.changeProductUnit(evt)
      }
    )
  }

  def getUnitPrice(): ServiceCall[GetUnitPriceCmd, UnitPriceVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         productDao.getUnitPrice(cmd)
      }
    )
  }

  def changeUnitPrice(): ServiceCall[ChangeUnitPriceCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = ChangeUnitPriceEvent(cmd.userId, cmd.productId, cmd.unitPrice)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        productDao.changeUnitPrice(evt)
      }
    )
  }

  def createOrder(): ServiceCall[CreateOrderCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = CreateOrderEvent(cmd.userId, cmd.orderId, cmd.orderTime, cmd.orderLines, cmd.orderPaymentType)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderDao.createOrder(evt)
      }
    )
  }

  def retrieveOrder(): ServiceCall[RetrieveOrderCmd, OrderVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        orderDao.retrieveOrder(cmd)
      }
    )
  }

  def updateOrder(): ServiceCall[UpdateOrderCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdateOrderEvent(cmd.userId, cmd.orderId, cmd.orderTime, cmd.orderLines, cmd.orderPaymentType)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderDao.updateOrder(evt)
      }
    )
  }

  def deleteOrder(): ServiceCall[DeleteOrderCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = DeleteOrderEvent(cmd.userId, cmd.orderId)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderDao.deleteOrder(evt)
      }
    )
  }

  def queryOrder(): ServiceCall[QueryCommand, OrderListVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         OrderListVo(orderDao.queryOrder(cmd))
      }
    )
  }

  def retrieveOrderByRowid(rowid: String): ServiceCall[NotUsed, OrderVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         orderDao.retrieveOrderByRowid(rowid)
      }
    )
  }

  def getOrderLines(): ServiceCall[GetOrderLinesCmd, OrderLinesVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         orderDao.getOrderLines(cmd)
      }
    )
  }

  def addOrderLines(): ServiceCall[AddOrderLinesCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = AddOrderLinesEvent(cmd.userId, cmd.orderId, cmd.orderLines)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderDao.addOrderLines(evt)
      }
    )
  }

  def removeOrderLines(): ServiceCall[RemoveOrderLinesCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = RemoveOrderLinesEvent(cmd.userId, cmd.orderId)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderDao.removeOrderLines(evt)
      }
    )
  }

  def getOrderPaymentType(): ServiceCall[GetOrderPaymentTypeCmd, OrderPaymentTypeVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         orderDao.getOrderPaymentType(cmd)
      }
    )
  }

  def changeOrderPaymentType(): ServiceCall[ChangeOrderPaymentTypeCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = ChangeOrderPaymentTypeEvent(cmd.userId, cmd.orderId, cmd.orderPaymentType)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderDao.changeOrderPaymentType(evt)
      }
    )
  }

  def createOrderItem(): ServiceCall[CreateOrderItemCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = CreateOrderItemEvent(cmd.userId, cmd.orderId, cmd.productId, cmd.productName, cmd.itemUnit, cmd.unitPrice, cmd.orderQuantity)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderItemDao.createOrderItem(evt)
      }
    )
  }

  def retrieveOrderItem(): ServiceCall[RetrieveOrderItemCmd, OrderItemVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        orderItemDao.retrieveOrderItem(cmd)
      }
    )
  }

  def updateOrderItem(): ServiceCall[UpdateOrderItemCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdateOrderItemEvent(cmd.userId, cmd.orderId, cmd.productId, cmd.productName, cmd.itemUnit, cmd.unitPrice, cmd.orderQuantity)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderItemDao.updateOrderItem(evt)
      }
    )
  }

  def deleteOrderItem(): ServiceCall[DeleteOrderItemCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = DeleteOrderItemEvent(cmd.userId, cmd.orderId, cmd.productId)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        orderItemDao.deleteOrderItem(evt)
      }
    )
  }

  def queryOrderItem(): ServiceCall[QueryCommand, OrderItemListVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         OrderItemListVo(orderItemDao.queryOrderItem(cmd))
      }
    )
  }

  def retrieveOrderItemByRowid(rowid: String): ServiceCall[NotUsed, OrderItemVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         orderItemDao.retrieveOrderItemByRowid(rowid)
      }
    )
  }

  def selectOrderItemByOrderId(orderId: String): ServiceCall[NotUsed, OrderItemListVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         OrderItemListVo(orderItemDao.selectByOrderId(orderId))
      }
    )
  }

  def deleteOrderItemByOrderId(orderId: String): ServiceCall[NotUsed, Int] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         orderItemDao.deleteByOrderId(orderId)
      }
    )
  }

  def selectOrderItemByProductId(productId: String): ServiceCall[NotUsed, OrderItemListVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         OrderItemListVo(orderItemDao.selectByProductId(productId))
      }
    )
  }

  def deleteOrderItemByProductId(productId: String): ServiceCall[NotUsed, Int] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         orderItemDao.deleteByProductId(productId)
      }
    )
  }

  def createEventJournal(): ServiceCall[CreateEventJournalCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = CreateEventJournalEvent(cmd.userId, cmd.persistenceId, cmd.occurredTime, cmd.metaData, cmd.content)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        eventJournalDao.createEventJournal(evt)
      }
    )
  }

  def retrieveEventJournal(): ServiceCall[RetrieveEventJournalCmd, EventJournalVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        eventJournalDao.retrieveEventJournal(cmd)
      }
    )
  }

  def updateEventJournal(): ServiceCall[UpdateEventJournalCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = UpdateEventJournalEvent(cmd.userId, cmd.persistenceId, cmd.occurredTime, cmd.metaData, cmd.content)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        eventJournalDao.updateEventJournal(evt)
      }
    )
  }

  def deleteEventJournal(): ServiceCall[DeleteEventJournalCmd, Int] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
        val evt = DeleteEventJournalEvent(cmd.userId, cmd.persistenceId, cmd.occurredTime)
        eventJournalDao.createEventJournal(
          CreateEventJournalEvent(cmd.userId, cmd.entityId, timeBased().toString, evt.getClass.getName, evt.toByteString)
        )
        eventJournalDao.deleteEventJournal(evt)
      }
    )
  }

  def queryEventJournal(): ServiceCall[QueryCommand, EventJournalListVo] = ServiceCall { cmd =>
    Future.successful(
      db.withTransaction { implicit c =>
         EventJournalListVo(eventJournalDao.queryEventJournal(cmd))
      }
    )
  }

  def retrieveEventJournalByRowid(rowid: String): ServiceCall[NotUsed, EventJournalVo] = ServiceCall { _ =>
    Future.successful(
      db.withTransaction { implicit c =>
         eventJournalDao.retrieveEventJournalByRowid(rowid)
      }
    )
  }

  def events(offset: Option[String]): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]] = {
    ServiceCall { is =>
      Future.successful(is.map(x => x))
    }
  }
}
