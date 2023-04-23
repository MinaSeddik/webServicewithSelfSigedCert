package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LogoutSuccessEventAction implements EventAction {
    private static final Action ACTION = Action.LOGOUT;

    private String message;


    @Override
    public Action actionType() {
        return ACTION;
    }
}