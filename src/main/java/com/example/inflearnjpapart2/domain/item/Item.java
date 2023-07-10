package com.example.inflearnjpapart2.domain.item;

import com.example.inflearnjpapart2.domain.Category;
import com.example.inflearnjpapart2.exception.NotEnoughException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // book이든, album이든, movie이든 상관 없이 한 테이블에 넣는다는 뜻
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==// -> 세터를 넣지 않고, 핵심 비즈니스 로직을 가지고 변경하는 것이 좋다. 도메인 주도 개발을 하는 것이 객체 지향적.
    /**
     * 재고 증가
     * **/
    public void addStock(int stockQuantity){
        this.stockQuantity += stockQuantity;
    }

    /**
     * 재고 감소
     */
    public void removeStock(int stockQuantity){
        int resStock = this.stockQuantity - stockQuantity;
        if(resStock < 0){
            throw new NotEnoughException("need more stock");
        }
        this.stockQuantity -= stockQuantity;
    }

}
