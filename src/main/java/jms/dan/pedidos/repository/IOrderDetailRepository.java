package jms.dan.pedidos.repository;

import jms.dan.pedidos.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
}
