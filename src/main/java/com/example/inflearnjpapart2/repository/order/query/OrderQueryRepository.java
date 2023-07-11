package com.example.inflearnjpapart2.repository.order.query;

import com.example.inflearnjpapart2.dto.OrderDto;
import com.example.inflearnjpapart2.dto.OrderFlatDto;
import com.example.inflearnjpapart2.dto.OrderItemDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // Dto를 사용하는 것 보다 Data select 하는 양이 줄어든다는 이점이 있지만, 코드가 복잡해진다는 단점이 있다.
    public List<OrderDto> findAllByDto_optimization() {
        // 쿼리 총 2번 나가는 솔루션
        List<OrderDto> orders = findOrders();
        // 쿼리 한 번으로 현재 order와 관련된 orderItem을 모두 가져옴
        Map<Long, List<OrderItemDto>> orderItemMap = findOrderItemMap(toOrderIds(orders));
        // order list에 각각의 order items를 세팅한다.
        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return orders;
    }

    private Map<Long, List<OrderItemDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemDto> orderItems = em.createQuery("select new com.example.inflearnjpapart2.dto.OrderItemDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi " +
                                "join oi.item i " +
                                "where oi.order.id in :orderIds",
                        OrderItemDto.class
                )
                .setParameter("orderIds", orderIds)
                .getResultList();

        // map으로  전환(위의 리스트에는 orderId와 상관 없이 order item이 섞여 있으므로 분류한다
        Map<Long, List<OrderItemDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDto::getOrderId));
        return orderItemMap;
    }

    private static List<Long> toOrderIds(List<OrderDto> orders) {
        return orders.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        // 쿼리 총 1번 나가는 솔루션
        // 장점: 쿼리가 한 번만 나간다
        // 단점: 페이징 가능하나 Order를 기준으로 페이징 불가능. Order가 뻥튀기 되기 떄문이다. (1:N 관계이기 때문에). 데이타가 많지 않으면 이게 훨씬 빠를 것.
        // 하지만 항상 쿼리가 적게 나간다고 좋은 것이 아님.
        return em.createQuery("select new com.example.inflearnjpapart2.dto.OrderFlatDto" +
                                "(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                                " from Order o " +
                                "join o.member m " +
                                "join o.delivery d " +
                                "join o.orderItems oi " +
                                "join oi.item i",
                        OrderFlatDto.class
                )
                .getResultList();
    }
}
