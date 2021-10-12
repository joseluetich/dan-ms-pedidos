package jms.dan.pedidos.repository;

import jms.dan.pedidos.dto.ClientDTO;
import jms.dan.pedidos.dto.ConstructionDTO;
import jms.dan.pedidos.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.Objects;

@Repository
public class ConstructionRepository implements IConstructionRepository {
    private static final String BASEURL = "http://dan-gateway:8080/users/api-users/";
    private static final String CONSTRUCTION_URL = BASEURL + "constructions/";
    private static final String CLIENTS_URL = BASEURL + "clients/";

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public ClientDTO getClientAssociated(Integer idConstruction) {
        Integer clientId = getClientIdAssociated(idConstruction);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        WebClient webClient = WebClient.create(CLIENTS_URL + clientId);

        return circuitBreaker.run(() -> {
            ResponseEntity<ClientDTO> response = webClient.get()
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(ClientDTO.class)
                    .block();
            ClientDTO client = null;
            if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                client = Objects.requireNonNull(response.getBody());
            }
            if (client == null) throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error has occurred - Client not found", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return client;
        } , throwable -> defaultClientAssociated());
    }

    private ClientDTO defaultClientAssociated() {
        System.out.println("********** CIRCUIT OPEN **********");
        return null;
    }

    public Integer getClientIdAssociated(Integer idConstruction) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        WebClient webClient = WebClient.create(CONSTRUCTION_URL + idConstruction);

        return circuitBreaker.run(() -> {
            ResponseEntity<ConstructionDTO> response = webClient.get()
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(ConstructionDTO.class)
                    .block();
            Integer clientId = null;
            if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                clientId = Objects.requireNonNull(response.getBody()).getClient().getId();
            }
            if (clientId == null) throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "An error has occurred - Client id not found", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return clientId;
        } , throwable -> defaultClientIdAssociated());
    }

    private Integer defaultClientIdAssociated() {
        System.out.println("********** CIRCUIT OPEN **********");
        return null;
    }
}
