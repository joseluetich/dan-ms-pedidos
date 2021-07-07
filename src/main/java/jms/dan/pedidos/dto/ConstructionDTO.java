package jms.dan.pedidos.dto;

public class ConstructionDTO {
    private Integer id;
    private String description;
    private Integer clientId;
    private ClientDTO client;

    public ConstructionDTO() {
    }

    public ConstructionDTO(Integer id, String description, Integer clientId, ClientDTO client) {
        this.id = id;
        this.description = description;
        this.clientId = clientId;
        this.client = client;
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

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }
}
