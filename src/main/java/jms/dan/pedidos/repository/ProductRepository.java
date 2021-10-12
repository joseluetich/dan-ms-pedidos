package jms.dan.pedidos.repository;

import jms.dan.pedidos.dto.ProductDTO;
import jms.dan.pedidos.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Repository
public class ProductRepository implements IProductRepository{
    private static final String BASEURL = "http://dan-gateway:8080";
    private static final String PRODUCTS_URL = BASEURL + "/products/api-products/products";

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    private Integer defaultClientIdAssociated() {
        System.out.println("********** CIRCUIT OPEN **********");
        return null;
    }

    @Override
    public ProductDTO getProductById(Integer idProduct) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        WebClient webClient = WebClient.create(PRODUCTS_URL + "/" + idProduct);

        return (ProductDTO) circuitBreaker.run(() -> {
            ResponseEntity<ProductDTO> response = webClient.get()
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(ProductDTO.class)
                    .block();
            ProductDTO productDTO = null;
            if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                productDTO = Objects.requireNonNull(response.getBody());
            } else {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        "An error has occurred - Product not found", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return productDTO;
        } , throwable -> defaultClientIdAssociated());
    }
}
