package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginSuccessEventAction implements EventAction {
    private static final Action ACTION = Action.SUCCEEDED_LOGIN;

    private String message;


    @Override
    public Action actionType() {
        return ACTION;
    }
}
