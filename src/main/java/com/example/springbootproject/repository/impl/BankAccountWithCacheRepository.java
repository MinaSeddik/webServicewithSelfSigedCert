package com.example.springbootproject.repository.impl;


import com.example.springbootproject.domain.BankAccount;
import com.example.springbootproject.repository.rowmapper.BankAccountRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class BankAccountWithCacheRepository {

    private final static String BANK_ACCOUNT_CACHE_NAME = "bankAccountCache";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = BANK_ACCOUNT_CACHE_NAME, key = "T(org.springframework.cache.interceptor.SimpleKey).EMPTY")
    public List<BankAccount> getAllAccounts() {

        log.info("getAllAccounts Repo called");
        return jdbcTemplate.query("select * from account", new BankAccountRowMapper());
    }

    @Cacheable(value = BANK_ACCOUNT_CACHE_NAME)
    public BankAccount getAccountById(int id) {

        log.info("getAccountById Repo called");
        return jdbcTemplate.queryForObject("select * from account where id = ?",
                new BankAccountRowMapper(), id);
    }

    @Cacheable(value = BANK_ACCOUNT_CACHE_NAME)
    public BankAccount getAccountByName(String firstName) {

        log.info("getAccountByName Repo called");
        return jdbcTemplate.queryForObject("select * from account where first_name = ?",
                new BankAccountRowMapper(), firstName);
    }

    // Don't Cache anything, just insert directly to the database
    public int insertAccountByName(String firstName, int balance) {

        log.info("insertAccountByName Repo called");

        // WATCH OUT: SQL INJECTION here
        String sql = "insert into account (first_name, balance, branch_id) values('" + firstName + "'," + balance + ", 3)";

        int insertedRows = jdbcTemplate.update(sql);
        log.info("Inserted row(s): {}", insertedRows);

        // we need to remove the list of cached entries
        Cache bankAccountCache = cacheManager.getCache(BANK_ACCOUNT_CACHE_NAME);
        if (insertedRows > 0 && Objects.nonNull(bankAccountCache)) {
            log.info("Clear Cache of the list of all items");
            bankAccountCache.evictIfPresent(SimpleKey.EMPTY);
        }

        return insertedRows;
    }

    public int updateAccountByNameAndGetUpdatedCount(String firstName) {

        log.info("updateAccountByName Repo called");
        Object[] args = new Object[]{firstName};
        String sql = "update account set balance = balance + 1000 where first_name = ?";
        int updatedRows = jdbcTemplate.update(sql, args);

        log.info("Updated row(s): {}", updatedRows);

        // we need to remove the list of cached entries and key entry if present
        Cache bankAccountCache = cacheManager.getCache(BANK_ACCOUNT_CACHE_NAME);
        if (updatedRows > 0 && Objects.nonNull(bankAccountCache)) {
            log.info("Clear Cache of the list of all items");
            bankAccountCache.evictIfPresent(SimpleKey.EMPTY);

            log.info("Clear Cache of the key item");
            bankAccountCache.evictIfPresent(firstName);
        }

        return updatedRows;
    }

    @CachePut(value = "bankAccountCache")
    public BankAccount updateAccountByName(String firstName) {

        log.info("updateAccountByName Repo called");
        Object[] args = new Object[]{firstName};
        String sql = "update account set balance = balance + 1000 where first_name = ?";
        int updatedRows = jdbcTemplate.update(sql, args);

        log.info("Updated row(s): {}", updatedRows);

        // we need to remove the list of cached entries
        Cache bankAccountCache = cacheManager.getCache(BANK_ACCOUNT_CACHE_NAME);
        if (updatedRows > 0 && Objects.nonNull(bankAccountCache)) {
            log.info("Clear Cache of the list of all items");
            bankAccountCache.evictIfPresent(SimpleKey.EMPTY);
        }

        return jdbcTemplate.queryForObject("select * from account where first_name = ?",
                new BankAccountRowMapper(), firstName);
    }


    @CacheEvict(value = "bankAccountCache")
    public int deleteAccountByName(String firstName) {
        log.info("deleteAccountByName Repo called");

        String sql = "delete from account where first_name = ?";
        Object[] args = new Object[]{firstName};
        int deletedRows = jdbcTemplate.update(sql, args);

        // we need to remove the list of cached entries
        Cache bankAccountCache = cacheManager.getCache(BANK_ACCOUNT_CACHE_NAME);
        if (deletedRows > 0 && Objects.nonNull(bankAccountCache)) {
            log.info("Clear Cache of the list of all items");
            bankAccountCache.evictIfPresent(SimpleKey.EMPTY);
        }

        return deletedRows;
    }

    @CacheEvict(value = "bankAccountCache")
    public int deleteAccountById(String id) {
        log.info("deleteAccountById Repo called");

        String sql = "delete from account where id = ?";
        Object[] args = new Object[]{id};
        int deletedRows = jdbcTemplate.update(sql, args);

        // we need to remove the list of cached entries
        Cache bankAccountCache = cacheManager.getCache(BANK_ACCOUNT_CACHE_NAME);
        if (deletedRows > 0 && Objects.nonNull(bankAccountCache)) {
            log.info("Clear Cache of the list of all items");
            bankAccountCache.evictIfPresent(SimpleKey.EMPTY);
        }

        return deletedRows;
    }

    @CacheEvict(value = "bankAccountCache", allEntries = true)
    public int deleteAccountsByPattern(String id) {
        log.info("deleteAccountId Repo called");

        String sql = "delete from account where id >= ?";
        Object[] args = new Object[]{id};
        return jdbcTemplate.update(sql, args);
    }

}
