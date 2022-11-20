package com.example.springbootproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestErrorResponse {

    private String timestamp;

    private String status;

    private String error;

    private String message;

    private String path;

    private String traceId;

    Map<String, String> validation;

}
