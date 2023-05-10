package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Post {

    private Long id;
    private String name;
    private Long timestamp;

    private AppUser creator;

    private List<Comment> comments = new ArrayList<>();

    private PostSettings settings;

}
