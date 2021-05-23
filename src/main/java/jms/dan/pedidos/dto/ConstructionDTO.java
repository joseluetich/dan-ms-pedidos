package jms.dan.pedidos.dto;

public class ConstructionDTO {
    private Integer id;
    private String description;
    private Integer clientId;

    public ConstructionDTO() {
    }

    public ConstructionDTO(Integer id, String description, Integer clientId) {
        this.id = id;
        this.description = description;
        this.clientId = clientId;
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

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}
