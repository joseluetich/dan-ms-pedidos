package jms.dan.pedidos.dto;

public class ProductDTO {
    private Integer id;
    private String description;
    private Double price;
    private Integer actualStock;
    private String name;

    public ProductDTO() {
    }

    public ProductDTO(Integer id, String description, Double price, Integer actualStock, String name) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.actualStock = actualStock;
        this.name = name;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getActualStock() {
        return actualStock;
    }

    public void setActualStock(Integer actualStock) {
        this.actualStock = actualStock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
