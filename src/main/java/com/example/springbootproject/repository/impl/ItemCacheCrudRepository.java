package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ItemCacheCrudRepository {

    private static int COUNTER = 0;

    private List<Item> items = new ArrayList<>();


//    @Autowired
//    private JdbcTemplate jdbcTemplate;


    @Cacheable(value = "items", key = "'all'")
    public List<Item> findAll() {
        log.info("ItemCacheCrudRepository: findAll");


        return items;
    }

    // tell Spring-cache not to cache null value in @Cacheable annotation
//    @Cacheable(value = "item", key = "#id", unless = "#result == null")
    @Cacheable(value = "item", key = "#id", unless = "#result.isEmpty()")
    public Optional<Item> findById(int id) {
        log.info("ItemCacheCrudRepository: findById");

        System.out.println("here ............");
        Optional<Item> itemOptional = items.stream()
                .filter(i -> i.getId() == id)
                .findFirst();

        return itemOptional;
    }

    @Caching(evict = {
            @CacheEvict(value = "items", allEntries = true),
            @CacheEvict(value = "item", allEntries = true)
    })
    public int saveOrUpdate(Item item) {
        log.info("ItemCacheCrudRepository: saveOrUpdate, {}", item.getTitle());

        return 1;
    }

    @CacheEvict(value = "items", allEntries = true)
    @CachePut(value = "item", key = "#result.id")
    public Item save(Item item) {
        log.info("ItemCacheCrudRepository: save, {}", item.getTitle());

        Item item1 = Item.builder()
                .id(++COUNTER)
                .title(item.getTitle())
                .desc(item.getDesc())
                .build();

        items.add(item1);

        return item1;
    }

    @CacheEvict(value = "items", allEntries = true)
    @CachePut(value = "item", key = "#item.id")
    public Item update_doAction(Item item) {
        log.info("ItemCacheCrudRepository:update_doAction, {}", item.getTitle());

        Item item1 = Item.builder()
                .id(item.getId())
                .title(item.getTitle())
                .desc(item.getDesc())
                .build();

        items.add(item1);

        return item1;
    }

}
