package com.example.springbootproject.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Payment {

    private String id;
}
