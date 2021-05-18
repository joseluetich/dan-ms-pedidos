package jms.dan.pedidos.rest;

import jms.dan.pedidos.domain.Client;
import jms.dan.pedidos.domain.Construction;
import jms.dan.pedidos.domain.Order;
import jms.dan.pedidos.domain.OrderDetail;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/orders")
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
        if (indexOpt.isPresent()) {
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            if (details != null) {
                details.add(newOrderDetail);
            } else {
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
        if (indexOpt.isPresent()) {
            ordersList.set(indexOpt.getAsInt(), newOrder);
            return ResponseEntity.ok(newOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Integer id) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(id))
                .findFirst();

        if (indexOpt.isPresent()) {
            ordersList.remove(indexOpt.getAsInt());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{idOrder}/detail/{idDetail}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Integer idOrder, @PathVariable Integer idDetail) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(idOrder))
                .findFirst();
        if (indexOpt.isPresent()) {
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            OptionalInt indexOptDetail = IntStream.range(0, details.size())
                    .filter(i -> details.get(i).getId().equals(idDetail))
                    .findFirst();
            if (indexOptDetail.isPresent()) {
                details.remove(indexOptDetail.getAsInt());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Optional<Order> order = ordersList
                .stream()
                .filter(or -> or.getId().equals(id))
                .findFirst();
        return ResponseEntity.of(order);
    }

    @GetMapping(path = "/{idOrder}/detail/{idDetail}")
    public ResponseEntity<OrderDetail> getOrderDetailById(@PathVariable Integer idOrder, @PathVariable Integer idDetail) {
        OptionalInt indexOpt = IntStream.range(0, ordersList.size())
                .filter(i -> ordersList.get(i).getId().equals(idOrder))
                .findFirst();
        if (indexOpt.isPresent()) {
            List<OrderDetail> details = ordersList.get(indexOpt.getAsInt()).getDetails();
            Optional<OrderDetail> orderDetail = details
                    .stream()
                    .filter(od -> od.getId().equals(idDetail))
                    .findFirst();
            return ResponseEntity.of(orderDetail);
        }
        return ResponseEntity.notFound().build();
    }

    // TODO it should filter by all params at same time
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam(required = false) Integer clientId, @RequestParam(required = false) String clientCUIT,
                                                 @RequestParam(required = false) Integer constructionId) {
        Integer clientIdExtra = null;
        if (constructionId != null) {
            List<Order> orders = ordersList
                    .stream()
                    .filter(or -> or.getConstruction().getId().equals(constructionId)).collect(Collectors.toList());
            return new ResponseEntity<>(orders, HttpStatus.OK);
        }
        if (clientCUIT != null) {
            WebClient webClient = WebClient.create("http://localhost:8080/api-users/clients/cuit/" + clientCUIT);
            try {
                ResponseEntity<Client> response = webClient.get()
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Client.class)
                        .block();
                clientIdExtra = response.getStatusCode().equals(HttpStatus.OK) ?
                        Objects.requireNonNull(response.getBody()).getId() : null;
            } catch (WebClientException e) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.ACCEPTED);
            }
        }

        if (clientId != null || clientIdExtra != null) {
            Integer client = clientId != null ? clientId : clientIdExtra;

            WebClient webClient = WebClient.create("http://localhost:8080/api-users/constructions?clientId=" + client);
            try {
                ResponseEntity<List<Construction>> response = webClient.get()
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntityList(Construction.class)
                        .block();
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    List<Construction> constructions = response.getBody();
                    if (constructions == null || constructions.isEmpty())
                        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                    List<Integer> constructionsId = constructions.stream().map(Construction::getId).collect(Collectors.toList());
                    List<Order> orders = ordersList
                            .stream()
                            .filter(or -> constructionsId.contains(or.getConstruction().getId())).collect(Collectors.toList());
                    return new ResponseEntity<>(orders, HttpStatus.OK);
                }
            } catch (WebClientException e) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.ACCEPTED);
            }
        }
        return ResponseEntity.ok(ordersList);
    }
}
