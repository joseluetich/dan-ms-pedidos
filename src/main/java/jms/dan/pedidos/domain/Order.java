package jms.dan.pedidos.domain;

import java.time.Instant;
import java.util.List;

public class Order {
    private Integer id;
    private Instant fechaPedido;
    private Construction construction;
    private List<OrderDetail> details;
    private OrderState state;

    public Order() {
    }

    public Order(Integer id, Instant fechaPedido, Construction construction, List<OrderDetail> details, OrderState state) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.construction = construction;
        this.details = details;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Instant fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Construction getConstruction() {
        return construction;
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }
}
