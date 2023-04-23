package com.example.springbootproject.audit;

import com.example.springbootproject.audit.event.Action;

import java.util.EnumMap;

public interface EventAction {

     EnumMap<Action, EventAction> actionMap = new EnumMap<>(Action.class);

     Action actionType();
}
