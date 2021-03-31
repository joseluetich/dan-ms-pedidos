package jms.dan.pedidos.domain;

public class Product {
    private Integer id;
    private String description;
    private Double precio;

    public Product() {
    }

    public Product(Integer id, String description, Double precio) {
        this.id = id;
        this.description = description;
        this.precio = precio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
