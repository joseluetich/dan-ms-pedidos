package jms.dan.pedidos.controller;

import jms.dan.pedidos.domain.Order;
import jms.dan.pedidos.domain.OrderDetail;
import jms.dan.pedidos.exceptions.ApiError;
import jms.dan.pedidos.exceptions.ApiException;
import jms.dan.pedidos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order newOrder) {
        try {
            orderService.createOrder(newOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @PostMapping(path = "/{id}/detail")
    public ResponseEntity<?> addOrderDetail(@RequestBody OrderDetail newOrderDetail, @PathVariable Integer id) {
        try {
            orderService.addOrderDetail(id, newOrderDetail);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order detail added successfully");
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateOrder(@RequestBody Order newOrder, @PathVariable Integer id) {
        try {
            orderService.updateOrder(id, newOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order updated successfully");
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Order deleted successfully");
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @DeleteMapping(path = "/{idOrder}/detail/{idDetail}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable Integer idOrder, @PathVariable Integer idDetail) {
        try {
            orderService.deleteOrderDetail(idOrder, idDetail);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Order detail deleted successfully");
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping(path = "/{idOrder}/detail/{idDetail}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable Integer idOrder, @PathVariable Integer idDetail) {
        try {
            OrderDetail orderDetail = orderService.getOrderDetailById(idOrder, idDetail);
            return new ResponseEntity<>(orderDetail, HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) Integer clientId, @RequestParam(required = false) String clientCUIT,
                                       @RequestParam(required = false) Integer constructionId) {
        try {
            List<Order> orders = orderService.getOrders(clientId, clientCUIT, constructionId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (ApiException e) {
            return new ResponseEntity<>(
                    new ApiError(e.getCode(), e.getDescription(), e.getStatusCode()),
                    HttpStatus.valueOf(e.getStatusCode()));
        }
    }
}
