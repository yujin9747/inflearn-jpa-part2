package com.example.inflearnjpapart2.repository.order.query;

import com.example.inflearnjpapart2.dto.OrderDto;
import com.example.inflearnjpapart2.dto.OrderItemDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderDto> findOrderQueryDtos() {
        // N+1 문제 존재함.
        List<OrderDto> result = findOrders(); // +1개
        result.forEach(orderDto -> {
            List<OrderItemDto> orderItems = findOrderItems(orderDto.getOrderId()); // N개
            orderDto.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderDto> findOrders() {
        return em.createQuery("select new com.example.inflearnjpapart2.dto.OrderDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o " +
                                "join o.member m " +
                                "join o.delivery d",
                        OrderDto.class
                )
                .getResultList();
    }

    private List<OrderItemDto> findOrderItems(Long orderId) {
        return em.createQuery("select new com.example.inflearnjpapart2.dto.OrderItemDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi " +
                                "join oi.item i " +
                                "where oi.order.id = :orderId",
                        OrderItemDto.class
                )
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
