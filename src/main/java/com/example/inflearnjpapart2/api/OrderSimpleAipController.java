package com.example.inflearnjpapart2.api;

import com.example.inflearnjpapart2.domain.Order;
import com.example.inflearnjpapart2.domain.OrderSearch;
import com.example.inflearnjpapart2.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne 관계 ( Many to One, One to One )
 * Order
 * Order -> Member
 * Order -> Delivery
 * */
@RestController
@RequiredArgsConstructor
public class OrderSimpleAipController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            // Lazy  강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }
}
