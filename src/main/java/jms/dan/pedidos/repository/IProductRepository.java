package jms.dan.pedidos.repository;

import jms.dan.pedidos.dto.ProductDTO;

public interface IProductRepository {

    ProductDTO getProductById(Integer idProduct);
}
