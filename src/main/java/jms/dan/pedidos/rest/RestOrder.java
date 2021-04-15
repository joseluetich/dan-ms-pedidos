package jms.dan.pedidos.rest;

import jms.dan.pedidos.domain.Order;
import jms.dan.pedidos.domain.OrderDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

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

    @PostMapping(path = "/{id}/detail")
    public ResponseEntity<OrderDetail> addOrderDetail(@RequestBody OrderDetail newOrderDetail, @PathVariable Integer id) {

        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(id))
                .findFirst();
        if (indexOpt.isPresent()){
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            if(details!=null) {
                details.add(newOrderDetail);
            }
            else {
                List<OrderDetail> newList = new ArrayList<>();
                newList.add(newOrderDetail);
                ordersList.get(indexOpt.getAsInt()).setDetails(newList);
            }
            return ResponseEntity.ok(newOrderDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Order> updateOrder(@RequestBody Order newOrder, @PathVariable Integer id) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(id))
                .findFirst();
        if (indexOpt.isPresent()){
            ordersList.set(indexOpt.getAsInt(), newOrder);
            return ResponseEntity.ok(newOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Integer id){
        OptionalInt indexOpt =   IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(id))
                .findFirst();

        if(indexOpt.isPresent()){
            ordersList.remove(indexOpt.getAsInt());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{idOrder}/detail/{idDetail}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Integer idOrder, @PathVariable Integer idDetail){
        OptionalInt indexOpt =   IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(idOrder))
                .findFirst();
        if(indexOpt.isPresent()){
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            OptionalInt indexOptDetail =   IntStream.range(0, details.size())
                    .filter(i -> details.get(i).getId().equals(idDetail))
                    .findFirst();
            if(indexOptDetail.isPresent()) {
                details.remove(indexOptDetail.getAsInt());
            }
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        return ResponseEntity.ok(ordersList);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id){
        Optional<Order> order = ordersList
                .stream()
                .filter(or -> or.getId().equals(id))
                .findFirst();
        return ResponseEntity.of(order);
    }

}