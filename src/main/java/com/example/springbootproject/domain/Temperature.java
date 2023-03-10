package com.example.springbootproject.domain;

public final class Temperature {
    private double value;

    public Temperature() {

    }

    public Temperature(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue() {
        this.value = value;
    }

    @Override
    public String toString() {
        return ((Number) value).toString();
    }
}