package jms.dan.pedidos.repository;

import jms.dan.pedidos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<Order, Integer> {

}
