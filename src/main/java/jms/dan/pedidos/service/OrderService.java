package jms.dan.pedidos.service;

import jms.dan.pedidos.dto.ClientDTO;
import jms.dan.pedidos.dto.ConstructionDTO;
import jms.dan.pedidos.dto.ProductDTO;
import jms.dan.pedidos.exceptions.ApiException;
import jms.dan.pedidos.model.Order;
import jms.dan.pedidos.model.OrderDetail;
import jms.dan.pedidos.model.OrderState;
import jms.dan.pedidos.repository.IConstructionRepository;
import jms.dan.pedidos.repository.IOrderDetailRepository;
import jms.dan.pedidos.repository.IOrderRepository;
import jms.dan.pedidos.repository.IOrderStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    final IOrderRepository orderRepository;
    final IConstructionRepository constructionRepository;
    final IOrderStateRepository orderStateRepository;
    final IOrderDetailRepository orderDetailRepository;
    final JmsTemplate jmsTemplate;

    @Autowired
    public OrderService(
            IOrderRepository orderRepository,
            IConstructionRepository constructionRepository,
            IOrderStateRepository orderStateRepository,
            IOrderDetailRepository orderDetailRepository,
            JmsTemplate jmsTemplate
    ) {
        this.orderRepository = orderRepository;
        this.constructionRepository = constructionRepository;
        this.orderStateRepository = orderStateRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void createOrder(Order newOrder) {
        validateOrder(newOrder);
        newOrder.setConstructionId(newOrder.getConstruction().getId());
        for(OrderDetail detail : newOrder.getDetails()){
            detail.setProductId(detail.getProduct().getId());
        }

        Order order = orderRepository.save(newOrder);

        List<Integer> orderDetailIds = new ArrayList<>();
        for (OrderDetail detail : order.getDetails()){
            orderDetailIds.add(detail.getId());
        }

        // TODO this ih throwing ActiveMQNotConnectedException[errorType=NOT_CONNECTED message=AMQ219007: Cannot connect to server(s)
        // jmsTemplate.convertAndSend("COLA_PEDIDOS", orderDetailIds);
    }

    @Override
    public void addOrderDetail(Integer orderId, OrderDetail newOrderDetail) {
        Order order = getOrderById(orderId);
        List<OrderDetail> details = order.getDetails();
        newOrderDetail.setProductId(newOrderDetail.getProduct().getId());
        details.add(newOrderDetail);
        if (checkProductStock(null, newOrderDetail)) {
            ClientDTO client = constructionRepository.getClientAssociated(order.getConstruction().getId());
            checkClientBalance(details, client);
            OrderState state = orderStateRepository.findById(1).orElse(null);
            order.setState(state);
        } else {
            OrderState state = orderStateRepository.findById(2).orElse(null);
            order.setState(state);
        }
        order.setDetails(details);
        orderRepository.save(order);
        orderDetailRepository.save(newOrderDetail);
    }

    @Override
    public Order updateOrder(Integer orderId, Order newOrder) {
        // SE RESUELVE NO MODIFICAR LA INSTANCIA POR CRITERIOS NO DEFINIDOS
        return getOrderById(orderId);
    }

    @Override
    public void deleteOrder(Integer orderId) {
        Order order = getOrderById(orderId);
        orderRepository.deleteById(order.getId());
    }

    @Override
    public void deleteOrderDetail(Integer orderId, Integer orderDetailId) {
        Order order = getOrderById(orderId);
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElse(null);
        OrderDetail detail = order.getDetails().stream().filter(od -> od.getId().equals(orderDetailId)).findFirst().orElse(null);
        if (detail == null || orderDetail == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order detail not found", HttpStatus.NOT_FOUND.value());
        }
        List<OrderDetail> details = order.getDetails().stream().filter(det -> !det.getId().equals(orderDetailId)).collect(Collectors.toList());
        order.setDetails(details);
        orderRepository.save(order);
        orderDetailRepository.deleteById(orderDetailId);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order not found", HttpStatus.NOT_FOUND.value());
        }
        return order;
    }

    @Override
    public OrderDetail getOrderDetailById(Integer orderId, Integer orderDetailId) {
        Order order = getOrderById(orderId);
        List<OrderDetail> details = order.getDetails();
        OrderDetail detail = details.stream().filter(orderDetail -> orderDetail.getId().equals(orderDetailId)).findFirst().orElse(null);
        if (detail == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.toString(), "Order detail not found", HttpStatus.NOT_FOUND.value());
        }
        return detail;
    }

    // TODO it should filter by all params at same time
    @Override
    public List<Order> getOrders(Integer clientId, String clientCuit, Integer constructionId) {
        Integer clientIdExtra = null;
        if (constructionId != null) {
            return orderRepository.findAll()
                    .stream()
                    .filter(or -> or.getConstructionId().equals(constructionId)).collect(Collectors.toList());
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
                    if (constructions == null || constructions.isEmpty()) return new ArrayList<>();

                    List<Integer> constructionsId = constructions.stream().map(ConstructionDTO::getId).collect(Collectors.toList());
                    return orderRepository.findAll()
                            .stream()
                            .filter(or -> constructionsId.contains(or.getConstructionId())).collect(Collectors.toList());
                }
            } catch (WebClientException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error has occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        return orderRepository.findAll();
    }

    public Boolean checkBCRACreditStatus(Integer clientId) {
        return true;
    }

    private void validateOrder(Order order) {
        if (checkProductStock(order.getDetails(), null)) {
            ClientDTO client = constructionRepository.getClientAssociated(order.getConstruction().getId());
            checkClientBalance(order.getDetails(), client);
            OrderState state = orderStateRepository.findById(1).orElse(null);
            order.setState(state);
        } else {
            OrderState state = orderStateRepository.findById(2).orElse(null);
            order.setState(state);
        }
    }

    private boolean checkProductStock(List<OrderDetail> orderDetails, OrderDetail orderDetail) {
        boolean check = true;
        if (orderDetail != null) {
            check = checkOrderStock(orderDetail);
        } else {
            for (OrderDetail detail : orderDetails) {
                check = checkOrderStock(detail);
                if (!check) break;
            }
        }
        return check;
    }

    private boolean checkOrderStock(OrderDetail orderDetail) {
        try {
            ResponseEntity<ProductDTO> response =
                    WebClient.create("http://localhost:8082/api-products/products/" + orderDetail.getProduct().getId()).get()
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .toEntity(ProductDTO.class)
                            .block();

            if (response != null && response.getBody() != null) {
                ProductDTO product = response.getBody();
                return product.getActualStock() >= orderDetail.getQuantity();
            }
        } catch (WebClientException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error has occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return false;
    }

    private void checkClientBalance(List<OrderDetail> ordersDetails, ClientDTO client) {
        Double balance = client.getCurrentBalance();
        for (OrderDetail orderDetail : ordersDetails) {
            balance -= (orderDetail.getPrice() * orderDetail.getQuantity());
        }

        if (balance < 0 && !(Math.abs(balance) < client.getMaxCurrentAccount() && checkBCRACreditStatus(client.getId()))) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "Client balance insufficient", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
