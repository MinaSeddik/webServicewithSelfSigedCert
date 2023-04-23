package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InviteNewUserEventAction implements EventAction {
    private static final Action ACTION = Action.INVITE_NEW_USER;

    private String invitedEmail;


    @Override
    public Action actionType() {
        return ACTION;
    }
}
