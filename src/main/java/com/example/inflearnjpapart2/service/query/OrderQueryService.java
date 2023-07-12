package com.example.inflearnjpapart2.service.query;

import com.example.inflearnjpapart2.domain.Order;
import com.example.inflearnjpapart2.dto.OrderDto;
import com.example.inflearnjpapart2.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDto::new)
                .toList();
    }
}
