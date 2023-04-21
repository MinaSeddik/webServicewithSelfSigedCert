package com.example.springbootproject.entity;

public enum UserTableColumn {

    NAME("name"),
    AGE("age"),
    ID("id");

    public final String COLUMN_NAME;

    UserTableColumn(String name) {
        COLUMN_NAME = name;
    }

    @Override
    public String toString() {
        return COLUMN_NAME;
    }

}