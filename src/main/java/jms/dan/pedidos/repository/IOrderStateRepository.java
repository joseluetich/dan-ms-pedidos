package jms.dan.pedidos.repository;

import jms.dan.pedidos.model.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderStateRepository extends JpaRepository<OrderState, Integer> {
}
