package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SystemDetail {

    private String system;
    private String environment;
    private String version;

}
