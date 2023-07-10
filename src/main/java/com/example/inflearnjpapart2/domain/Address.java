package com.example.inflearnjpapart2.domain;

import jakarta.persistence.Embeddable;
import lombok.Setter;

@Embeddable
@Setter
public class Address {
    String city;
    String street;
    String zipcode;
}
