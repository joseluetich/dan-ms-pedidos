package jms.dan.pedidos.repository;

import jms.dan.pedidos.domain.Order;
import jms.dan.pedidos.domain.OrderDetail;

import java.util.List;

public interface IOrderRepository {
    void createOrder(Order newOrder);

    void deleteOrder(Integer orderId);

    Order updateOrder(Integer orderId, Order newOrder);

    void addOrderDetail(Integer orderId, OrderDetail newOrderDetail);

    void deleteOrderDetail(Integer orderId, Integer orderDetailId);

    Order getOrderById(Integer orderId);

    OrderDetail getOrderDetailById(Integer orderId, Integer orderDetailId);

    List<Order> getOrders();
}
