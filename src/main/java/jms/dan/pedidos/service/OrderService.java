package jms.dan.pedidos.service;

import jms.dan.pedidos.domain.Order;
import jms.dan.pedidos.domain.OrderDetail;
import jms.dan.pedidos.dto.ClientDTO;
import jms.dan.pedidos.dto.ConstructionDTO;
import jms.dan.pedidos.dto.ProductDTO;
import jms.dan.pedidos.exceptions.ApiException;
import jms.dan.pedidos.repository.IConstructionRepository;
import jms.dan.pedidos.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    final OrderRepository orderRepository;
    final IConstructionRepository constructionRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, IConstructionRepository constructionRepository) {
        this.orderRepository = orderRepository;
        this.constructionRepository = constructionRepository;
    }

    @Override
    public void createOrder(Order newOrder) {
        checkProductStock(newOrder.getDetails(), null);
        ClientDTO client = constructionRepository.getClientAssociated(newOrder.getConstruction().getId());
        checkClientBalance(newOrder.getDetails(), client.getCurrentBalance());
        orderRepository.createOrder(newOrder);
    }

    @Override
    public void addOrderDetail(Integer orderId, OrderDetail newOrderDetail) {
        checkProductStock(null, newOrderDetail);
        Order order = orderRepository.getOrderById(orderId);
        List<OrderDetail> orderDetails = new ArrayList<>(order.getDetails());
        orderDetails.add(newOrderDetail);
        ClientDTO client = constructionRepository.getClientAssociated(order.getConstruction().getId());
        checkClientBalance(orderDetails, client.getCurrentBalance());
        orderRepository.addOrderDetail(orderId, newOrderDetail);
    }

    @Override
    public Order updateOrder(Integer orderId, Order newOrder) {
        checkProductStock(newOrder.getDetails(), null);
        ClientDTO client = constructionRepository.getClientAssociated(newOrder.getConstruction().getId());
        checkClientBalance(newOrder.getDetails(), client.getCurrentBalance());
        Order orderUpdated = orderRepository.updateOrder(orderId, newOrder);
        if (orderUpdated == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order not found", HttpStatus.NOT_FOUND.value());
        }
        return orderUpdated;
    }

    private void checkProductStock(List<OrderDetail> orderDetails, OrderDetail orderDetail) {
        if (orderDetail != null) {
            try {
                ResponseEntity<ProductDTO> response =
                        WebClient.create("http://localhost:8082/api-products/products/" + orderDetail.getProduct().getId()).get()
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .toEntity(ProductDTO.class)
                                .block();

                if (response != null && response.getBody() != null) {
                    ProductDTO product = response.getBody();
                    if (product.getActualStock() < orderDetail.getQuantity()) {
                        throw new ApiException(HttpStatus.BAD_REQUEST.toString(),
                                "Product " + product.getName() + " does not have stock.",
                                HttpStatus.BAD_REQUEST.value());
                    }
                }
            } catch (WebClientException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error has occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else {
            for (OrderDetail detail : orderDetails) {
                try {
                    ResponseEntity<ProductDTO> response =
                            WebClient.create("http://localhost:8082/api-products/products/" + detail.getProduct().getId()).get()
                                    .accept(MediaType.APPLICATION_JSON)
                                    .retrieve()
                                    .toEntity(ProductDTO.class)
                                    .block();

                    if (response != null && response.getBody() != null) {
                        ProductDTO product = response.getBody();
                        if (product.getActualStock() < detail.getQuantity()) {
                            throw new ApiException(HttpStatus.BAD_REQUEST.toString(),
                                    "Product " + product.getName() + " does not have stock.",
                                    HttpStatus.BAD_REQUEST.value());
                        }
                    }
                } catch (WebClientException e) {
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error has occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
        }
    }

    private void checkClientBalance(List<OrderDetail> ordersDetails, Double clientBalance) {
        Double balance = clientBalance;
        for (OrderDetail orderDetail : ordersDetails) {
            if (balance >= 0) balance -= (orderDetail.getPrice()*orderDetail.getQuantity());
            else break;
        }
        if (balance < 0) throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Client balance insufficient", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Override
    public void deleteOrder(Integer orderId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order not found", HttpStatus.NOT_FOUND.value());
        }
        orderRepository.deleteOrder(orderId);
    }

    @Override
    public void deleteOrderDetail(Integer orderId, Integer orderDetailId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order not found", HttpStatus.NOT_FOUND.value());
        }
        OrderDetail orderDetail = orderRepository.getOrderDetailById(orderId, orderDetailId);
        if (orderDetail == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order detail not found", HttpStatus.NOT_FOUND.value());
        }
        orderRepository.deleteOrderDetail(orderId, orderDetailId);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order not found", HttpStatus.NOT_FOUND.value());
        }
        return order;
    }

    @Override
    public OrderDetail getOrderDetailById(Integer orderId, Integer orderDetailId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order not found", HttpStatus.NOT_FOUND.value());
        }
        OrderDetail orderDetail = orderRepository.getOrderDetailById(orderId, orderDetailId);
        if (orderDetail == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order detail not found", HttpStatus.NOT_FOUND.value());
        }
        return orderDetail;
    }

    // TODO it should filter by all params at same time
    @Override
    public List<Order> getOrders(Integer clientId, String clientCuit, Integer constructionId) {
        Integer clientIdExtra = null;
        if (constructionId != null) {
            return orderRepository.getOrders()
                    .stream()
                    .filter(or -> or.getConstruction().getId().equals(constructionId)).collect(Collectors.toList());
        }
        if (clientCuit != null) {
            WebClient webClient = WebClient.create("http://localhost:8080/api-users/clients/cuit/" + clientCuit);
            try {
                ResponseEntity<ClientDTO> response = webClient.get()
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(ClientDTO.class)
                        .block();
                clientIdExtra = response.getStatusCode().equals(HttpStatus.OK) ?
                        Objects.requireNonNull(response.getBody()).getId() : null;
            } catch (WebClientException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error has occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

        if (clientId != null || clientIdExtra != null) {
            Integer client = clientId != null ? clientId : clientIdExtra;

            WebClient webClient = WebClient.create("http://localhost:8080/api-users/constructions?clientId=" + client);
            try {
                ResponseEntity<List<ConstructionDTO>> response = webClient.get()
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntityList(ConstructionDTO.class)
                        .block();
                if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                    List<ConstructionDTO> constructions = response.getBody();
                    if (constructions == null || constructions.isEmpty())
                        return new ArrayList<>();
                    List<Integer> constructionsId = constructions.stream().map(ConstructionDTO::getId).collect(Collectors.toList());
                    return orderRepository.getOrders()
                            .stream()
                            .filter(or -> constructionsId.contains(or.getConstruction().getId())).collect(Collectors.toList());
                }
            } catch (WebClientException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error has occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        return orderRepository.getOrders();
    }
}
