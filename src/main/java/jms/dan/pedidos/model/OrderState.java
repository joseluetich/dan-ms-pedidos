package jms.dan.pedidos.model;

public class OrderState {
    private Integer id;
    private String state;

    public OrderState() {
    }

    public OrderState(Integer id, String state) {
        super();
        this.id = id;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
