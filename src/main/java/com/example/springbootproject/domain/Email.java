package com.example.springbootproject.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Email {

    private int id;

    private String from;

    private String to;

    private String cc;

    private String subject;

    private String body;

}
