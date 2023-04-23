package com.example.springbootproject.audit.event;

public enum Action {

    SUCCEEDED_LOGIN("Succeeded Login"),
    FAILED_LOGIN("Failed Login"),
    UN_AUTHORIZED_REQUEST("Un-Authorized Request"),
    LOGOUT("Logout"),
    INVITE_NEW_USER("Invite New User"),
    CANCEL_ORDER("Cancel Order"),
    FAILED_ORDER_CREATION("Failed Create Order"),
    RETRY_ORDER_CREATION("Retry Create Order"),
    CREATE_ORDER("Create Order"),
    EDIT_ORDER("Edit Order");


    private final String action;

    Action(String action) {
        this.action = action;
    }


}
