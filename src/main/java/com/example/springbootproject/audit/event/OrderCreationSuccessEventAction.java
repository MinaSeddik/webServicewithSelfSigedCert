package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.domain.Order;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderCreationSuccessEventAction implements EventAction {
    private static final Action ACTION = Action.CREATE_ORDER;

    private Order order;

    private String paymentCode;


    @Override
    public Action actionType() {
        return ACTION;
    }

}
