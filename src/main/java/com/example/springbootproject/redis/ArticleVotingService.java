package com.example.springbootproject.redis;


import com.example.springbootproject.domain.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ArticleVotingService {

    private final static long ONE_WEEK_IN_SECONDS = 7 * 86400;
    private final static int VOTE_SCORE = 432;
    private final static int ARTICLES_PER_PAGE = 25;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public long postArticle(String user, String title, String link) {

        log.info("postArticle here 1 ---------------");
        long articleId = redisTemplate.opsForValue().increment("article:");
        log.info("postArticle here 2 ---------------");

        String voted = "voted:" + articleId;
        redisTemplate.opsForSet().add(voted, user);
        redisTemplate.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);

        String article = "article:" + articleId;
        long now = System.currentTimeMillis();

        Map<String, Object> articleMap = new HashMap<>();
        articleMap.put("title", title);
        articleMap.put("link", link);
        articleMap.put("poster", user);
        articleMap.put("time", now);
        articleMap.put("votes", 1);
        redisTemplate.opsForHash().putAll(article, articleMap);

        redisTemplate.opsForZSet().add("score:", article, now + VOTE_SCORE);
        redisTemplate.opsForZSet().add("time:", article, now);

        return articleId;
    }


    public void voteArticle(String user, String article) {

        long cutoff = System.currentTimeMillis() - ONE_WEEK_IN_SECONDS;
        if (redisTemplate.opsForZSet().score("time:", article).longValue() < cutoff) {
            return;
        }

        String articleId = article.split(":")[1];
        if (redisTemplate.opsForSet().add("voted:" + articleId, user) > 0) {
            redisTemplate.opsForZSet().incrementScore("score:", article, VOTE_SCORE);
            redisTemplate.opsForHash().increment(article, "votes", 1);
        }

    }

    public List<Article> getArticles(int page) {
        return getArticles(page, "score:");
    }

    public List<Article> getArticles(int page, String key) {

        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;

        Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, start, end);

        List<Article> articleList = new ArrayList<>();
        for (String id : ids) {

//            List<Object> articleData = redisTemplate.opsForHash().multiGet(id,
//                    Arrays.asList("title", "link", "user", "poster", "time", "votes"));
            Article article = Article.of(id,
                    redisTemplate.opsForHash().get(id, "title").toString(),
                    redisTemplate.opsForHash().get(id, "link").toString(),
                    redisTemplate.opsForHash().get(id, "poster").toString(),
                    Long.parseLong(redisTemplate.opsForHash().get(id, "time").toString()),
                    Integer.parseInt(redisTemplate.opsForHash().get(id, "votes").toString())
            );
            articleList.add(article);
        }

        return articleList;
    }

    public void addRemoveGroups(String articleId, List<String> addGroups, List<String> removeGroups) {

        String article = "article:" + articleId;
        for (String group : addGroups) {
            redisTemplate.opsForSet().add("group:" + group, article);
        }

        for (String group : removeGroups) {
            redisTemplate.opsForSet().remove("group:" + group, article);
        }
    }


    public List<Article> getGroupArticles(String group, int page) {

        String key = "score:" + group;
        if(!redisTemplate.hasKey(key)){

            redisTemplate.opsForZSet()
                    .intersectAndStore(key, Arrays.asList("group:" + group, "score:"),
                                    "", RedisZSetCommands.Aggregate.MAX);

            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }

        return getArticles(page, key);
    }

    public long postArticle2(String user, String title, String link) {

        log.info("postArticle here 1 ---------------");
        long articleId = redisTemplate.opsForValue().increment("article:");
        log.info("postArticle here 2 ---------------");

        //execute a transaction
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                operations.opsForSet().add("key", "value1");

                // This will contain the results of all operations in the transaction
                return operations.exec();
            }
        });
        System.out.println("Number of items added to set: " + txResults.get(0));

        return articleId;
    }

    @Transactional
    public long postArticle3(String user, String title, String link) {

        log.info("postArticle here 1 ---------------");
        long articleId = redisTemplate.opsForValue().increment("article:");
        log.info("postArticle here 2 ---------------");

        //execute a transaction
        List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {

                operations.multi();
                operations.opsForSet().add("key", "value1");

                // This will contain the results of all operations in the transaction
//                return operations.exec();
                return null;
            }
        });
        System.out.println("Number of items added to set: " + txResults.get(0));

        return articleId;
    }

}
