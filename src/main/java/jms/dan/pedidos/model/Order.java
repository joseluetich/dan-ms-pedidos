package jms.dan.pedidos.model;

import jms.dan.pedidos.dto.ConstructionDTO;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Instant orderDate;
    @Transient
    private ConstructionDTO construction;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderDetail> details;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private OrderState state;

    public Order() {
    }

    public Order(Integer id, Instant orderDate, ConstructionDTO construction, List<OrderDetail> details, OrderState state) {
        this.id = id;
        this.orderDate = orderDate;
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

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public ConstructionDTO getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionDTO construction) {
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
