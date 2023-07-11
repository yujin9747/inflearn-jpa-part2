package com.example.inflearnjpapart2.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) // db에서 가져올 때 order만 가져오고 member는 new ProxyMember를 해서 넣어 둔다. (ByteBuddyInterceptor 역할)
    @JoinColumn(name = "member_id")
    private Member member; // Member를 뽑으려고 하는데 프록시 객체가 있는 경우 type definition error 발생.

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL  )
    @JoinColumn(name = "delivery_id")
    private Delevery delivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delevery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public static Order createOrder(Member member, Delevery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
}
