package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthorizationFailureEventAction implements EventAction {
    private static final Action ACTION = Action.UN_AUTHORIZED_REQUEST;

    private String message;


    @Override
    public Action actionType() {
        return ACTION;
    }
}
