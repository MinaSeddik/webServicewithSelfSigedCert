package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MyOrder {

    private int orderId;
    private String orderCode;
    private String applicantName;
    private OrderType orderType;
    private int attempts;
}
