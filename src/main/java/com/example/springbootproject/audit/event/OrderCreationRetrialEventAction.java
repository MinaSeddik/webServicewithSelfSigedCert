package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.domain.MyOrder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderCreationRetrialEventAction implements EventAction {
    private static final Action ACTION = Action.RETRY_ORDER_CREATION;

    private MyOrder order;

    private int retrialNumber;

    private String message;


    @Override
    public Action actionType() {
        return ACTION;
    }
}