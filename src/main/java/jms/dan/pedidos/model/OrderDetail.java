package jms.dan.pedidos.model;

import jms.dan.pedidos.dto.ProductDTO;

public class OrderDetail {
    private Integer id;
    private ProductDTO product;
    private Integer quantity;
    private Double price;

    public OrderDetail() {
    }

    public OrderDetail(ProductDTO product, Integer quantity, Double price) {
        super();
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
