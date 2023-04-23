package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderCancellationEventAction implements EventAction {
    private static final Action ACTION = Action.CANCEL_ORDER;

    private String orderCode;
    private String applicantName;
    private String orderType;
    private String message;


    @Override
    public Action actionType() {
        return ACTION;
    }
}