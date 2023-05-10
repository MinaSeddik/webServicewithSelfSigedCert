package com.example.springbootproject.dto;


import com.example.springbootproject.domain.PostSettings;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

    private Long id;
    private String name;
    private Long creationDate;

    private String username;
    private int commentCount;

    private List<CommentDto> comments = new ArrayList<>();

    private Map<String, String> settings;


}
