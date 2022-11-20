package com.example.springbootproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse {

    private String timestamp;

    private String status;

    private String username;

    private String authorities;

    private String message;

    private Map<String, String> data;

}
