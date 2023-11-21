package com.example.springbootproject.actuator;

import com.example.springbootproject.actuator.model.MyAppCacheInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

// to enable this endpoint, you should out it into management.endpoints.web.exposure.include in application.properties

@Component
@Endpoint(id = "cache-manager")
@Slf4j
public class CacheManagerEndpoint {

    @Autowired
    private CacheManager cacheManager;

    /*
        GET
        http://localhost:8080/actuator/cache-manager
     */
    @ReadOperation
    public List<MyAppCacheInfo> getAllCacheInfo() {
//        return cacheManager.getCacheNames()
//                .stream().map(cacheManager::getCache)
//                .map(cache -> MyAppCacheInfo.builder().cacheName(cache.getName())
//                        .cacheSize(((com.github.benmanes.caffeine.cache.Cache) cache.getNativeCache()).asMap().size())
//                        .build())
//                .collect(Collectors.toList());


        return cacheManager.getCacheNames()
                .stream().map(cacheManager::getCache)
                .map(cache -> {

                    ConcurrentMap map = ((com.github.benmanes.caffeine.cache.Cache) cache.getNativeCache()).asMap();

                    Set<Map.Entry> set = map.entrySet();
                    for (Map.Entry o : set) {

                        log.info("Key Class: {}", o.getKey().getClass());
                        log.info("Value Class: {}", o.getValue().getClass());

                        log.info("Key= {}", o.getKey());
                        log.info("Value= {}", o.getValue());

                        log.info("------------------------");

                    }


                    return MyAppCacheInfo.builder().cacheName(cache.getName())
                            .cacheSize(((com.github.benmanes.caffeine.cache.Cache) cache.getNativeCache()).asMap().size())
                            .build();
                })
                .collect(Collectors.toList());

    }

    /*
        GET
        http://localhost:8080/actuator/cache-manager/{cacheName}
     */
    @ReadOperation
    public MyAppCacheInfo getCacheInfo(@Selector String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (Objects.nonNull(cache)) {
            return MyAppCacheInfo.builder().cacheName(cache.getName())
                    .cacheSize(((com.github.benmanes.caffeine.cache.Cache) cache.getNativeCache()).asMap().size())
                    .build();
        }

        return MyAppCacheInfo.builder().build();
    }

    /*
        POST
        http://localhost:8080/actuator/cache-manager
     */
    @WriteOperation
    public void evictAll() {

        cacheManager.getCacheNames()
                .stream()
                .map(cacheManager::getCache)
                .forEach(cache -> cache.invalidate());
    }

    /*
        POST
        http://localhost:8080/actuator/cache-manager/{cacheName}
    */
    @WriteOperation
    public void evictCache(@Selector String cacheName) {

        Cache cache = cacheManager.getCache(cacheName);
        if (Objects.nonNull(cache)) {
            cache.invalidate();
        }
    }
}



