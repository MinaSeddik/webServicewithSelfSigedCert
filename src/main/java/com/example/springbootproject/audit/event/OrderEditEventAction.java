package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import com.example.springbootproject.domain.MyOrder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderEditEventAction implements EventAction {
    private static final Action ACTION = Action.EDIT_ORDER;

    private MyOrder oldOrder;
    private MyOrder order;


    @Override
    public Action actionType() {
        return ACTION;
    }

}
