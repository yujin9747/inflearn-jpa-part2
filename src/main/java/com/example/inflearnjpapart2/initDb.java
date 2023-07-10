package com.example.inflearnjpapart2;

import com.example.inflearnjpapart2.domain.*;
import com.example.inflearnjpapart2.domain.item.Book;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserA
 *  - JPA1
 *  - JPA2
 *
 *  UserB
 *  - SPRING1
 *  - SPRING2
 * */
@Component
@RequiredArgsConstructor
public class initDb {

    private final initService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
    }
    @Component
    @Transactional
    @RequiredArgsConstructor
    static class initService{
        private final EntityManager em;
        public void dbInit1(){
            Member memberA = createMember("UserA");
            Member memberB = createMember("UserB");
            em.persist(memberA);
            em.persist(memberB);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            Book book3 = createBook("Spring1 BOOK", 20000, 200);
            Book book4 = createBook("Spring2 BOOK", 40000, 300);
            em.persist(book1);
            em.persist(book2);
            em.persist(book3);
            em.persist(book4);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
            OrderItem orderItem3 = OrderItem.createOrderItem(book3, 20000, 3);
            OrderItem orderItem4 = OrderItem.createOrderItem(book4, 40000, 4);

            Delevery deleveryA = createDelivery(memberA);
            Delevery deleveryB = createDelivery(memberB);

            Order orderA = Order.createOrder(memberA, deleveryA, orderItem1, orderItem2);
            Order orderB = Order.createOrder(memberB, deleveryB, orderItem3, orderItem4);
            em.persist(orderA);
            em.persist(orderB);
        }

        private static Delevery createDelivery(Member memberA) {
            Delevery delivery = new Delevery();
            delivery.setAddress(memberA.getAddress());
            return delivery;
        }

        private static Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }

        private static Member createMember(String name) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address("서울", "1", "1111"));
            return member;
        }
    }
}
