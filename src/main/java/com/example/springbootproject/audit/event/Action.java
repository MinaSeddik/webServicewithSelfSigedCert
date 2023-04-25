package com.example.springbootproject.audit.event;

import com.example.springbootproject.audit.EventAction;

public enum Action {

    SUCCEEDED_LOGIN("Succeeded Login", LoginSuccessEventAction.class),
    FAILED_LOGIN("Failed Login", LoginFailureEventAction.class),
    UN_AUTHORIZED_REQUEST("Un-Authorized Request", AuthorizationFailureEventAction.class),
    LOGOUT("Logout", LogoutSuccessEventAction.class),
    INVITE_NEW_USER("Invite New User", InviteNewUserEventAction.class),
    CANCEL_ORDER("Cancel Order", OrderCancellationEventAction.class),
    FAILED_ORDER_CREATION("Failed Create Order", OrderCreationFailureEventAction.class),
    RETRY_ORDER_CREATION("Retry Create Order", OrderCreationRetrialEventAction.class),
    CREATE_ORDER("Create Order", OrderCreationSuccessEventAction.class),
    EDIT_ORDER("Edit Order", OrderEditEventAction.class);


    private final String action;
    private final Class<? extends EventAction> eventActionClass;

    Action(String action, Class<? extends EventAction> eventActionClass) {
        this.action = action;
        this.eventActionClass = eventActionClass;
    }

    public Class<? extends EventAction> getHandlerClass(){
        return eventActionClass;
    }

    public String getActionText() {
        return this.action;
    }

    public static Action resolveAction(String actionText) {
        for (Action a : Action.values()) {
            if (a.getActionText().equalsIgnoreCase(actionText)) {
                return a;
            }
        }
        return null;
    }

}
