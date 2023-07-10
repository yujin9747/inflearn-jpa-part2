package com.example.inflearnjpapart2.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    String city;
    String street;
    String zipcode;
}
