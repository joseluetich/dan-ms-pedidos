package jms.dan.pedidos.repository;

import jms.dan.pedidos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Integer> {
    // void createOrder(Order newOrder);

    // void deleteOrder(Integer orderId);

    // Order updateOrder(Integer orderId, Order newOrder);

    // void addOrderDetail(Integer orderId, OrderDetail newOrderDetail);

    // void deleteOrderDetail(Integer orderId, Integer orderDetailId);

    Order getOrderById(Integer orderId);

    // OrderDetail getOrderDetailById(Integer orderId, Integer orderDetailId);

    // List<Order> getOrders();
}
