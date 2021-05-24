package jms.dan.pedidos.repository;

import jms.dan.pedidos.model.Order;
import jms.dan.pedidos.model.OrderDetail;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Repository
public class OrderRepository implements IOrderRepository {
    private static final List<Order> ordersList = new ArrayList<>();
    private static Integer ID_GEN = 1;

    @Override
    public void createOrder(Order newOrder) {
        newOrder.setId(ID_GEN++);
        ordersList.add(newOrder);
    }

    @Override
    public void deleteOrder(Integer orderId) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size()).filter(i -> ordersList.get(i).getId().equals(orderId)).findFirst();
        if (indexOpt.isPresent()) {
            ordersList.remove(indexOpt.getAsInt());
        }
    }

    @Override
    public Order updateOrder(Integer orderId, Order newOrder) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(orderId))
                .findFirst();
        if (indexOpt.isPresent()) {
            ordersList.set(indexOpt.getAsInt(), newOrder);
            return newOrder;
        }
        return null;
    }

    @Override
    public void addOrderDetail(Integer orderId, OrderDetail newOrderDetail) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(orderId))
                .findFirst();
        if (indexOpt.isPresent()) {
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            if (details != null) {
                details.add(newOrderDetail);
            } else {
                List<OrderDetail> newList = new ArrayList<>();
                newList.add(newOrderDetail);
                ordersList.get(indexOpt.getAsInt()).setDetails(newList);
            }
        }
    }

    @Override
    public void deleteOrderDetail(Integer orderId, Integer orderDetailId) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(orderId))
                .findFirst();
        if (indexOpt.isPresent()) {
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            OptionalInt indexOptDetail = IntStream.range(0, details.size())
                    .filter(i -> details.get(i).getId().equals(orderDetailId))
                    .findFirst();
            if (indexOptDetail.isPresent()) {
                details.remove(indexOptDetail.getAsInt());
            }
        }
    }

    @Override
    public Order getOrderById(Integer orderId) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size()).filter(i -> ordersList.get(i).getId().equals(orderId)).findFirst();
        if (indexOpt.isPresent()) {
            return ordersList.get(indexOpt.getAsInt());
        }
        return null;
    }

    @Override
    public OrderDetail getOrderDetailById(Integer orderId, Integer orderDetailId) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(orderId))
                .findFirst();
        if (indexOpt.isPresent()) {
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            OptionalInt indexOrderDetail = IntStream.range(0, details.size())
                    .filter(i -> ordersList.get(i).getId().equals(orderDetailId))
                    .findFirst();
            if (indexOrderDetail.isPresent()) {
                return details.get(indexOrderDetail.getAsInt());
            }
        }
        return null;
    }

    @Override
    public List<Order> getOrders() {
        return ordersList;
    }
}
