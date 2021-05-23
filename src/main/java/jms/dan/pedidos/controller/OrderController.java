package jms.dan.pedidos.controller;

import jms.dan.pedidos.domain.Client;
import jms.dan.pedidos.domain.Construction;
import jms.dan.pedidos.domain.Order;
import jms.dan.pedidos.domain.OrderDetail;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/orders")
public class OrderController {
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
    public ResponseEntity<Order> deleteOrderDetail(@PathVariable Integer id) {
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

    // TODO It Should filter by all params at same time
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam(required = false) Integer clientId, @RequestParam(required = false) String clientCUIT,
                                                 @RequestParam(required = false) Integer constructionId) {
        Integer clientIdExtra = null;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        if (constructionId != null) {
            List<Order> orders = ordersList
                    .stream()
                    .filter(or -> or.getConstruction().getId().equals(constructionId)).collect(Collectors.toList());
            return new ResponseEntity<>(orders, HttpStatus.OK);
        }
        if (clientCUIT != null) {
            String url = "http://localhost:8081/api-users/clients/cuit/" + clientCUIT;
            ResponseEntity<Client> responseEntity;
            try {
                responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Client.class);
                clientIdExtra = responseEntity.getStatusCode().equals(HttpStatus.OK) ?
                        Objects.requireNonNull(responseEntity.getBody()).getId() : null;
            } catch (HttpClientErrorException exception) {
                return new ResponseEntity<>(new ArrayList<>(), exception.getStatusCode());
            }
        }

        if (clientId != null || clientIdExtra != null) {
            Integer client = clientId != null ? clientId : clientIdExtra;
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/api-users/constructions")
                    .queryParam("clientId", client);
            ResponseEntity<List<Construction>> responseEntity = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Construction>>() {
                    });

            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                List<Construction> constructions = responseEntity.getBody();
                if (constructions == null || constructions.isEmpty())
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                List<Integer> constructionsId = constructions.stream().map(Construction::getId).collect(Collectors.toList());
                List<Order> orders = ordersList
                        .stream()
                        .filter(or -> constructionsId.contains(or.getConstruction().getId())).collect(Collectors.toList());
                return new ResponseEntity<>(orders, HttpStatus.OK);
            }
        }
        return ResponseEntity.ok(ordersList);
    }
}
