package com.example.springbootproject.service;

import com.example.springbootproject.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class PostService {


    public Post getSomePost() {

        long timeStamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        AppUser creator = AppUser.builder()
                .id(5)
                .username("David")
                .password("ENCRPYTED_PWD")
                .algorithm(EncryptionAlgorithm.BCRYPT)
                .authorities(List.of("ADMIN_ROLE"))
                .mfa(false)
                .build();

        Comment comment1 = Comment.builder()
                .id(102L)
                .data("This is the first comment")
                .build();

        Comment comment2 = Comment.builder()
                .id(102L)
                .data("This is the second comment")
                .build();

        PostSettings postSettings = PostSettings.builder()
                .maxPosts(500)
                .length(600)
                .build();

        return Post.builder()
                .id(1L)
                .name("")
                .timestamp(timeStamp)
                .creator(creator)
                .comments(List.of(comment1, comment2))
                .settings(postSettings)
                .build();
    }


    public Post findById(long l) {

        return getSomePost();
    }

    public Post updatePost(String id, Post post) {

        // do the logic or update

        return getSomePost();
    }

    public Post createPost(Post post) {

        // do the logic for create post

        return getSomePost();
    }

    public void deletePost(String id) {

        // do the logic for delete post
    }
}
