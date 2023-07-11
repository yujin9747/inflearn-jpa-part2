package com.example.inflearnjpapart2.repository.order.simplequery;

import com.example.inflearnjpapart2.dto.SimpleOrderDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;


    // simple query만 따로 분리하는 것이 좋다.
    public List<SimpleOrderDto> findOrderDtos() {
        return em.createQuery("select new com.example.inflearnjpapart2.dto.SimpleOrderDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", SimpleOrderDto.class) // proxy가 아닌 진짜 객체를 가져옴
                .getResultList();
    }

}
