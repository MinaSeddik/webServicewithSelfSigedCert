package com.example.springbootproject.audit.event;


//Reference: https://github.com/gchq/event-logging
public enum ___AuthenticateAction {

    LOGON("Logon"),
    LOGOFF("Logoff"),
    CHANGE_PASSWORD("ChangePassword"),
    RESET_PASSWORD("ResetPassword"),
    SCREEN_LOCK("ScreenLock"),
    SCREEN_UNLOCK("ScreenUnlock"),
    ACCOUNT_LOCK("AccountLock"),
    ACCOUNT_UNLOCK("AccountUnlock"),
    OTHER("Other");

    private final String value;

    ___AuthenticateAction(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ___AuthenticateAction fromValue(String v) {
        for (___AuthenticateAction c: ___AuthenticateAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
