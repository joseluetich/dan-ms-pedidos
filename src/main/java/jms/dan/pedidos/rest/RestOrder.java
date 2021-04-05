package jms.dan.pedidos.rest;

import jms.dan.pedidos.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class RestOrder {
    private static final List<Order> ordersList = new ArrayList<>();
    private static Integer ID_GEN = 1;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order newOrder) {
        newOrder.setId(ID_GEN++);
        ordersList.add(newOrder);
        return ResponseEntity.ok(newOrder);
    }
}