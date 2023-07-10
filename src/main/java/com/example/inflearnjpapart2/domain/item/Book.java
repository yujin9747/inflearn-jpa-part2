package com.example.inflearnjpapart2.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("B")    // 하나의 Item 테이블에 들어갈 때 Book임을 구분해주는 값
public class Book extends Item {

    private String author;
    private String isbn ;

}
