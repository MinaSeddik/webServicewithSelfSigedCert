package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class EventTime {

    private Date timeCreated;

}
