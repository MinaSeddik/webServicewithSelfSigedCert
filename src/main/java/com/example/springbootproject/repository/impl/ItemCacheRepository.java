package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class ItemCacheRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;


//    @Cacheable(value = "itemCache", key = "T(org.springframework.cache.interceptor.SimpleKey).EMPTY")
//    @Cacheable(value = "itemCache")
//    @Cacheable(value="itemCache", key="{ #root.methodName, #id }")
    @Cacheable(value="itemCache", key="'all'")
    public List<Item> getAllItems(int id) {

        log.info("########################   -    ItemCacheRepository::getAllItems Repo called");
//        return jdbcTemplate.query("select * from item", new ItemRowMapper());

        return Arrays.asList(Item.builder().id(id).desc("desc").build());
    }


}
