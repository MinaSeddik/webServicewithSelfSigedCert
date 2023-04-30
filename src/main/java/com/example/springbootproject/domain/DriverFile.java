package com.example.springbootproject.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DriverFile {
    private String kind;
    private String mimeType;
    private String id;
    private String name;
}
