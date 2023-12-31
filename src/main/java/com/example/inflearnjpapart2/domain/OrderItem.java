package com.example.inflearnjpapart2.domain;

import com.example.inflearnjpapart2.domain.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    private int orderPrice; // 주문 당시 가격
    private int count;  // 주문 당시 수량

    // 생성메서드 이외에 다른 방식을 쓰지 않도록 막는 용도 -> @NoArgsConstructor(access = AccessLevel.PROTECTED)로 대체 가능
//    protected OrderItem(){
//
//    }



    //==비즈니스 로직==//
    public void cancel() {
        // item의 재고 늘리기
        item.addStock(count);
    }

    //==조회 로직==//
    public int getTotalPrice() {
        return orderPrice * count;
    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }
}
