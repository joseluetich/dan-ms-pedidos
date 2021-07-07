package jms.dan.pedidos.service;

import jms.dan.pedidos.model.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail getOrderDetailById(Integer id);

    List<OrderDetail> getDetails();
}
