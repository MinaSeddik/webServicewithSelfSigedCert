package com.example.springbootproject.dto;

import com.example.springbootproject.domain.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {

    @JsonProperty("id")
    private int orderId;

    @JsonProperty("code")
    private String orderCode;

    private String applicantName;

    private OrderType orderType;

    @JsonIgnore
    private int attempts;


}
