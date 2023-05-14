package com.example.springbootproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class DateRange {

    private LocalDate startDate;

    private LocalDate endDate;

}
