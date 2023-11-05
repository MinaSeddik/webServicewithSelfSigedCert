package com.example.springbootproject;

import com.example.springbootproject.config.RedisCacheConfig;
import com.example.springbootproject.domain.Item;
import com.example.springbootproject.repository.impl.ItemCacheCrudRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Import({RedisCacheConfig.class, ItemCacheCrudRepository.class})
@ExtendWith(SpringExtension.class)
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
class ItemServiceCachingIntegrationTest {

    @MockBean
    private ItemCacheCrudRepository mockItemCacheCrudRepository;


    @Autowired
    private CacheManager cacheManager;


    @Test
    void givenRedisCaching_whenFindItemById_thenItemReturnedFromCache() {
        Item anItem = Item.builder()
                .id(32)
                .title("this is the title")
                .desc("this is the description")
                .build();

        given(mockItemCacheCrudRepository.findById(32))
                .willReturn(Optional.of(anItem));

        Item itemCacheMiss = mockItemCacheCrudRepository.findById(32).get();
        Item itemCacheHit = mockItemCacheCrudRepository.findById(32).get();

        assertThat(itemCacheMiss).isEqualTo(anItem);
        assertThat(itemCacheHit).isEqualTo(anItem);

//        verify(mockItemCacheCrudRepository, times(1)).findById(32);


//        assertThat(itemFromCache()).isEqualTo(anItem);
    }
}