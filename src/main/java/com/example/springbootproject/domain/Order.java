package com.example.springbootproject.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Order {

    private int orderId;
    private String orderCode;
    private String applicantName;
    private OrderType orderType;
    private int attempts;

}
