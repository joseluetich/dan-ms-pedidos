package jms.dan.pedidos.dto;

public class ClientDTO {
    private Integer id;
    private String businessName;
    private String cuit;
    private String mail;

    public ClientDTO () {}

    public ClientDTO(Integer id, String businessName, String cuit, String mail) {
        this.id = id;
        this.businessName = businessName;
        this.cuit = cuit;
        this.mail = mail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
