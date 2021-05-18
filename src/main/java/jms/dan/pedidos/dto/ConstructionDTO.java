package jms.dan.pedidos.dto;

public class ConstructionDTO {
    private Integer id;
    private String description;

    public ConstructionDTO() {
    }

    public ConstructionDTO(Integer id, String description) {
        this.id = id;
        this.description = description;
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
}
