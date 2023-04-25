package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumSet;

@Builder
@Getter
@Setter
public class Order {

    private int orderId;
    private String orderCode;
    private String applicantName;
    private OrderType orderType;
    private int attempts;
}
