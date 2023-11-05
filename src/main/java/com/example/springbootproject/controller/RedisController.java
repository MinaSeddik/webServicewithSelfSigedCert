package com.example.springbootproject.controller;


import com.example.springbootproject.domain.Article;
import com.example.springbootproject.domain.Email;
import com.example.springbootproject.domain.Item;
import com.example.springbootproject.redis.ArticleVotingService;
import com.example.springbootproject.redis.RedisPublisherService;
import com.example.springbootproject.repository.impl.ItemCacheCrudRepository;
import com.example.springbootproject.repository.impl.ItemCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class RedisController {

    @Autowired
    private ArticleVotingService articleVotingService;

    @Autowired
    private RedisPublisherService redisPublisherService;

    @Autowired
    private ItemCacheRepository itemCacheRepository;

    @Autowired
    private ItemCacheCrudRepository itemCacheCrudRepository;


    @RequestMapping(value = "/redis-articles")
    public List<Article> articles() throws RuntimeException {

        log.info("Article here ---------------");


        long articleId1 = articleVotingService.postArticle("mina","First Article - title", "https://www.google.com/");
        log.info("Article {} Created", articleId1);

        long articleId2 = articleVotingService.postArticle("mina","Second Article - title", "https://www.google.com/");
        log.info("Article {} Created", articleId2);

        long articleId3 = articleVotingService.postArticle("mina","Third Article - title", "https://www.google.com/");
        log.info("Article {} Created", articleId3);

        log.info("Vote article {}", articleId1);
        articleVotingService.voteArticle("user1", "article:" + articleId1);

        log.info("Vote article {}", articleId1);
        articleVotingService.voteArticle("user2", "article:" + articleId1);

        log.info("Vote article {}", articleId1);
        articleVotingService.voteArticle("user3", "article:" + articleId1);

        log.info("Vote article {}", articleId1);
        articleVotingService.voteArticle("user1", "article:" + articleId1);

        log.info("get articles ...");
        List<Article> articles = articleVotingService.getArticles(1);

        return articles;
    }

    @RequestMapping(value = "/redis-emails")
    public void emails() throws RuntimeException {
        IntStream.range(0,100).forEach(i ->
                redisPublisherService.publish(
                        Email.of(i+1, "x", "x", "x", "x", "x")));

    }

    @RequestMapping(value = "/redis-cache")
    public List<Item> redisCache() throws RuntimeException {

        log.info("redisCache here ---------------");

//        List<Item> items = itemCacheRepository.getAllItems(5);

        Item item = Item.builder()
                .title("title")
                .desc("dedc")
                .build();

        Item result = itemCacheCrudRepository.save(item);
        result = itemCacheCrudRepository.save(item);
        result = itemCacheCrudRepository.save(item);
        result = itemCacheCrudRepository.save(item);
        result = itemCacheCrudRepository.save(item);

        item = Item.builder()
                .id(3)
                .title("title________________")
                .desc("dedc___________")
                .build();
        result = itemCacheCrudRepository.update_doAction(item);

        List<Item> items = itemCacheCrudRepository.findAll();
        items = itemCacheCrudRepository.findAll();

        return items;
    }

}
