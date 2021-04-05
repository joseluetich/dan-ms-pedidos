package jms.dan.pedidos.domain;

public class OrderDetail {
    private Integer id;
    private Product product;
    private Integer quantity;
    private Double price;

    public OrderDetail() {
    }

    public OrderDetail(Product product, Integer quantity, Double price) {
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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
