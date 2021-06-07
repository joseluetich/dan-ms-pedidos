package jms.dan.pedidos.controller;

import jms.dan.pedidos.exceptions.ApiError;
import jms.dan.pedidos.exceptions.ApiException;
import jms.dan.pedidos.model.OrderDetail;
import jms.dan.pedidos.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/details")
public class OrderDetailController {
    final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable Integer id) {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
            return new ResponseEntity<>(orderDetail, HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getDetails() {
        try {
            List<OrderDetail> details = orderDetailService.getDetails();
            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }
}
