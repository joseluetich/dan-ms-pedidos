package jms.dan.pedidos.dto;

public class ClientDTO {
    private Integer id;
    private String businessName;
    private String cuit;
    private String mail;
    private Double currentBalance;
    private Double maxCurrentAccount;

    public ClientDTO () {}

    public ClientDTO(Integer id, String businessName, String cuit, String mail, Double currentBalance, Double maxCurrentAccount) {
        this.id = id;
        this.businessName = businessName;
        this.cuit = cuit;
        this.mail = mail;
        this.currentBalance = currentBalance;
        this.maxCurrentAccount = maxCurrentAccount;
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

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getMaxCurrentAccount() {
        return maxCurrentAccount;
    }

    public void setMaxCurrentAccount(Double maxCurrentAccount) {
        this.maxCurrentAccount = maxCurrentAccount;
    }
}
