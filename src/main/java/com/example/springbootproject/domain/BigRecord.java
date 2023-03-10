package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BigRecord {
    private String Series_reference;
    private Double Period;
    private String Data_value;
    private String Suppressed;
    private String STATUS;
    private String UNITS;
    private Integer MAGNTUDE;
    private String Subject;
    private String Grp;
    private String Series_title_1;
}