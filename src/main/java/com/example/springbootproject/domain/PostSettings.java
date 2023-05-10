package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSettings {

    private int maxPosts;
    private int length;

}
