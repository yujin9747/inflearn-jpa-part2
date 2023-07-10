package com.example.inflearnjpapart2.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@AllArgsConstructor
public class Address {
    String city;
    String street;
    String zipcode;

    public Address() {

    }
}
