package jms.dan.pedidos.service;

import jms.dan.pedidos.exceptions.ApiException;
import jms.dan.pedidos.model.OrderDetail;
import jms.dan.pedidos.repository.IOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService implements IOrderDetailService {
    final IOrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(IOrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public OrderDetail getOrderDetailById(Integer id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id).orElse(null);
        if (orderDetail == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order Detail not found", HttpStatus.NOT_FOUND.value());
        }
        return orderDetail;
    }

    @Override
    public List<OrderDetail> getDetails() {
        return orderDetailRepository.findAll();
    }
}
