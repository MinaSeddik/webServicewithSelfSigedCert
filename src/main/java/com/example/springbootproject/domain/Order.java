package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.EnumSet;

@Builder
@Getter
public class Order {

    private int orderId;
    private String orderCode;
    private String applicantName;
    private OrderType orderType;
}
