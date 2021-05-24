package jms.dan.pedidos.service;

import jms.dan.pedidos.model.Order;
import jms.dan.pedidos.model.OrderDetail;

import java.util.List;

public interface IOrderService {
    void createOrder(Order newOrder);

    void deleteOrder(Integer orderId);

    Order updateOrder(Integer orderId, Order newOrder);

    void addOrderDetail(Integer orderId, OrderDetail newOrderDetail);

    void deleteOrderDetail(Integer orderId, Integer orderDetailId);

    Order getOrderById(Integer orderId);

    OrderDetail getOrderDetailById(Integer orderId, Integer orderDetailId);

    List<Order> getOrders(Integer clientId, String clientCuit, Integer constructionId);
}
