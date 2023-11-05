package com.example.springbootproject.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
//@NoArgsConstructor
@ToString
@Builder
public class Item implements Serializable {

    private int id;

    private String title;

    private String link;

    private String desc;

    private long time;

    private int votes;
}
