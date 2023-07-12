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

    public List<Order> findAllWithItem() {
        // 근데 내가 직접 해보니까 distinct 붙이지 않아도 뻥튀기 되지 않음.
        // 하지만 query에도 distinct를 넣어준다는 것이 차이점.
        // distinct를 해도 db 쿼리에서 distinct를 하지 못하는 이유 => 모든 컬럼의 값이 같아야 해서 아래의 상황에서는 안된다.
        // 애플리케이션 단에서 중복을 제거해준다.
        /**치명적인 단점:
         * - 1:N를 페치조인 하는 순간 페이징 불가능 -> 쿼리에는 limit, offset 지정 X, 메모리 단에서 페이징함.
         * - 컬렉션 페치 조인은 1개만 사용 가
         * */
        return em.createQuery("select distinct o from Order o" + // distinct를 넣어주는 이유는 1:N 관계에서 데이터가 뻥튀기 되는 것을 막기 위해서
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                .setFirstResult(1)
                .setMaxResults(100) // WARNING: firstResult/maxResults specified with collection fetch; applying in memory
                .getResultList();
    }

    // query 수는 v3보단 늘어났지만 페이징이 가능하다. 또한 order items까지 조인하고 가져올 필요가 없으므로 성능이 오히려 좋아질 수 있다.
    // v3는 쿼리는 한 번에 나가지만 데이터가 많아지면 성능이 떨어질 수 있다.
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        // member와 delivery를 빼도 batch size 때문에 최적화가 된다. -> 네트워크를 더 많이 탄다는 단점이 있으므로 그냥 fetch join으로 잡는게 좋다.
//        return em.createQuery("select o from Order o", Order.class)
//                .setFirstResult(offset)
//                .setMaxResults(limit)
//                .getResultList();
        return em.createQuery("select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
