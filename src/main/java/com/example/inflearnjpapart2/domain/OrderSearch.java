package com.example.inflearnjpapart2.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName;          // 회원이름
    private OrderStatus orderStatus;    // ORDER, CANCEL
}
