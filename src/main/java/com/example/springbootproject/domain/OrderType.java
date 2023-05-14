package com.example.springbootproject.domain;

public enum OrderType {

    FBI("FBI"),

    FDLE("Florida"),
    CA("CA"),

    P2C("Print-to-Card");

    private final String value;

    private OrderType(String val) {
        value = val;
    }


    public static OrderType fromString(String value) {
        for (OrderType o : OrderType.values()) {
            if (o.value.equalsIgnoreCase(value)) {
                return o;
            }
        }
        return null;
    }
}
