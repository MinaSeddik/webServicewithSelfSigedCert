package com.example.springbootproject.domain;

public enum OrderType {

    FBI("FBI"),

    FDLE("Florida"),

    P2C("Print-to-Card");

    private final String value;

    private OrderType(String val) {
        value = val;
    }


}
