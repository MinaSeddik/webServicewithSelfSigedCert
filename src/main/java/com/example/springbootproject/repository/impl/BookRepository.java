package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Book;
import com.example.springbootproject.domain.BookSearchCriteria;
import com.example.springbootproject.repository.rowmapper.BookRowMapper;
import com.example.springbootproject.util.WhereClosureQueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class BookRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page<Book> getBooks(PageRequest pageRequest) {

        log.info("getBooks Repo called");

//        String title = "filteredTitle";


//        String sql = "SELECT * FROM book WHERE title = ? ";
//        String CountSql = "SELECT COUNT(*) FROM book WHERE title = ? ";

        String query = "SELECT * FROM book ";
        String countSql = "SELECT COUNT(*) FROM book ";


        pageRequest.isPaged();
        pageRequest.getSort().isSorted();


        String orderBy = " ORDER BY " + pageRequest.getSort().get() + " " + pageRequest.getSort().toList().get(0).getDirection().name();
        String limitOffset = " LIMIT " + pageRequest.getPageSize() + " OFFSET " + pageRequest.getOffset();

        String sql = query + orderBy + limitOffset;
        log.info("SQL = {}", sql);

        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper());
//        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), title);

        int count = 0;
        try {
            log.info("Count SQL = {}", countSql);

            count = jdbcTemplate.queryForObject(countSql, Integer.class);
//         count = jdbcTemplate.queryForObject(countSql, Integer.class, title);

        } catch (EmptyResultDataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("", e);
            }
        }

        log.info("Count = {}", count);


        return new PageImpl<Book>(books, pageRequest, count);
    }

    public Page<Book> getBooks2(BookSearchCriteria searchCriteria, PageRequest pageRequest) {

        log.info("getBooks Repo called");

        String query = "SELECT * FROM book ";
        String countSql = "SELECT COUNT(*) FROM book ";


        pageRequest.isPaged();
        pageRequest.getSort().isSorted();


        String whereClause = " WHERE title = ? OR author = ? ";
        String orderBy = " ORDER BY " + pageRequest.getSort().get() + " " + pageRequest.getSort().toList().get(0).getDirection().name();
        String limitOffset = " LIMIT " + pageRequest.getPageSize() + " OFFSET " + pageRequest.getOffset();

        String sql = query + whereClause + orderBy + limitOffset;
        log.info("SQL = {}", sql);

        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper());
//        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), title);

        int count = 0;
        try {

            sql = countSql + whereClause;
            log.info("Count SQL = {}", sql);

            count = jdbcTemplate.queryForObject(sql, Integer.class);
//         count = jdbcTemplate.queryForObject(sql, Integer.class, title);

        } catch (EmptyResultDataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("", e);
            }
        }

        log.info("Count = {}", count);


        return new PageImpl<Book>(books, pageRequest, count);
    }

    public Page<Book> getBooks(String searchTerm, PageRequest pageRequest) {

        log.info("getBooks Repo called");

        String query = "SELECT * FROM book ";
        String countSql = "SELECT COUNT(*) FROM book ";


        pageRequest.isPaged();
        pageRequest.getSort().isSorted();


        String whereClause = " WHERE title = ? OR author = ? ";
        String orderBy = " ORDER BY " + pageRequest.getSort().get() + " " + pageRequest.getSort().toList().get(0).getDirection().name();
        String limitOffset = " LIMIT " + pageRequest.getPageSize() + " OFFSET " + pageRequest.getOffset();

        String sql = query + whereClause + orderBy + limitOffset;
        log.info("SQL = {}", sql);

        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), searchTerm, searchTerm);
//        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), title);

        int count = 0;
        try {

            sql = countSql + whereClause;
            log.info("Count SQL = {}", sql);

            count = jdbcTemplate.queryForObject(sql, Integer.class);
//         count = jdbcTemplate.queryForObject(sql, Integer.class, title);

        } catch (EmptyResultDataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("", e);
            }
        }

        log.info("Count = {}", count);


        return new PageImpl<Book>(books, pageRequest, count);
    }

    public Page<Book> getBooks(BookSearchCriteria searchCriteria, Pageable pageable) {

        log.info("getBooks Repo called");

        String query = "SELECT * FROM book ";
        String countSql = "SELECT COUNT(*) FROM book ";

        // Handle Where Clause
        String whereClause = StringUtils.EMPTY;
        List<Object> sqlParameter = Collections.emptyList();
        if (searchCriteria != null) {
            WhereClosureQueryBuilder whereClosureQueryBuilder = new WhereClosureQueryBuilder(searchCriteria);
            String sqlClause = whereClosureQueryBuilder.getSqlClause();

            if (StringUtils.isNotBlank(sqlClause)) {
                whereClause = " WHERE ( " + sqlClause + " ) ";
                sqlParameter = whereClosureQueryBuilder.getSqlParameter();
            }
        }


        // Handle ORDER BY Clause
        String orderBy = StringUtils.EMPTY;
        if (pageable.getSort().isSorted()) {

            orderBy = pageable.getSort()
                    .get()
                    .map(order -> order.getProperty() + " " + order.getDirection().name())
                    .collect(Collectors.joining(",", " ORDER BY ", " "));
        }

        // Handle Pagination, LIMIT AND OFFSET
        String limitOffset = StringUtils.EMPTY;
        if (pageable.isPaged()) {
            limitOffset = " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        }

        String sql = query + whereClause + orderBy + limitOffset;
        log.info("SQL = {}", sql);

        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), sqlParameter.toArray());

        int count = 0;
        try {

            sql = countSql + whereClause;
            log.info("Count SQL = {}", sql);

            count = jdbcTemplate.queryForObject(sql, Integer.class, sqlParameter.toArray());

        } catch (EmptyResultDataAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("", e);
            }
        }

        log.info("Count = {}", count);
        return new PageImpl<>(books, pageable, count);

    }
}
