package com.example.springbootproject.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Builder
public class Book {

    private int year;

    private long id;

    @NotBlank
    @Size(min = 0, max = 20)
    @JsonProperty("xx__title_custom_json_name")
    private String title;

    @NotBlank
    @Size(min = 0, max = 30)
    private String author;

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
