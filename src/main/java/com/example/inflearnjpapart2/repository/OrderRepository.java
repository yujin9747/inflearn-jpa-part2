package com.example.inflearnjpapart2.repository;

import com.example.inflearnjpapart2.domain.Order;
import com.example.inflearnjpapart2.domain.OrderSearch;
import com.example.inflearnjpapart2.dto.SimpleOrderDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.orderStatus = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

    }


    // v3의 장점 : 내부의 원하는 것만 페치 조인해서 성능 조인 가능
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class) // proxy가 아닌 진짜 객체를 가져옴. lazy loading을 하지 않음
                .getResultList();
    }

    // v4는 재사용성이 줄어든다는 단점이 있다. dto로 조회한거는 인티티가 아니라서 변경감지 불가. 코드가 더 지저분함.
    // 장점은 v3보다는 성능 측면에서 더 낫다.
    // v4로 바꾼다고 해서 성능에 큰 영향을 주지는 않는다. 데이터 사이즈가 너무 큰 경우(필드가 너무 많은 경우)와 트래픽이 아주 많은 경우에는 고민해볼만 함.
    public List<SimpleOrderDto> findOrderDtos() {
        return em.createQuery("select new com.example.inflearnjpapart2.dto.SimpleOrderDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", SimpleOrderDto.class) // proxy가 아닌 진짜 객체를 가져옴
                .getResultList();
    }

}
