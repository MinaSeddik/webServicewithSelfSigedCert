package com.example.springbootproject.model;

import lombok.Data;

@Data
public class Book {

    private String title;
    private int year;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXx " + title);
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
