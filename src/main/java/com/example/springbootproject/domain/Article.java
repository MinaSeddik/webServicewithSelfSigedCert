package com.example.springbootproject.domain;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor(staticName = "of")
public class Article {

    private String id;

    private String title;

    private String link;

    private String user;

    private long time;

    private int votes;

}
