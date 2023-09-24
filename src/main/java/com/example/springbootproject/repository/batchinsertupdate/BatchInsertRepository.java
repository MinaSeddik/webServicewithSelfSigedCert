package com.example.springbootproject.repository.batchinsertupdate;

import com.example.springbootproject.domain.User2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class BatchInsertRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    /*
            Reference: https://javabydeveloper.com/spring-jdbctemplate-batch-update-with-maxperformance/

            Key point:
            ===========
            with rewriteBatchedStatements=true the JDBC will pack as many queries as possible into
            a single network packet, lowering this way the network overhead.
            rewriteBatchedStatements=true

            Make sure that "max_allowed_packet" server variable allows max bulk insertion size
            SHOW VARIABLES LIKE 'max_allowed_packet';

            USE the following line in application.properties:
            spring.datasource.jdbc-url=jdbc:mysql://localhost:3306/myapp?rewriteBatchedStatements=true

            Recommendation: Use batchInsert2 function instead for better java implementation

            to debug JdbcTemplate statements, put the following line in application.properties
            logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchInsert(List<User2> users) {
        StopWatch timer = new StopWatch();

        String sql = "INSERT INTO `USER2` (username, password, firstName, lastName, email, phoneNumber, active, locked, date_of_birth)"
                + " VALUES(?,?,?,?,?,?,?,?,?)";

        timer.start();
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User2 user = users.get(i);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getFirstName());
                ps.setString(4, user.getLastName());
                ps.setString(5, user.getEmail());
                ps.setString(6, user.getPhoneNumber());
                ps.setBoolean(7, user.isActive());
                ps.setBoolean(8, user.isLocked());
                ps.setDate(9, user.getDateOfBirth());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });

        timer.stop();
        log.info("batchInsert -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    /*
        Reference: https://javabydeveloper.com/spring-jdbctemplate-batch-update-with-maxperformance/

        Key point:
        ===========
        with rewriteBatchedStatements=true the JDBC will pack as many queries as possible into
        a single network packet, lowering this way the network overhead.
        rewriteBatchedStatements=true

        Make sure that "max_allowed_packet" server variable allows max bulk insertion size
        SHOW VARIABLES LIKE 'max_allowed_packet';

        USE the following line in application.properties:
        spring.datasource.jdbc-url=jdbc:mysql://localhost:3306/myapp?rewriteBatchedStatements=true

        to debug JdbcTemplate statements, put the following line in application.properties
        logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG


        Can we do better?
        Yes, JdbcTemplate runs the bulk statement using one single connection from the configured
        connection pool, it will be better to use multiple connection.
        USE batchInsertAsync function for better performance
    */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchInsert2(List<User2> users) {
        StopWatch timer = new StopWatch();

        String sql = "INSERT INTO user2 " +
                "(username, password, firstName, lastName, email, phoneNumber, active, locked, date_of_birth)"
                + " VALUES(?,?,?,?,?,?,?,?,?);";

        timer.start();
        int[] rowEffected;

        try {
            rowEffected = jdbcTemplate.batchUpdate(sql, new UserBatchPreparedStatementSetter(users));
        } catch (DataAccessException e) {
            log.error("DataAccessException : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        timer.stop();
        log.info("batchInsert rowEffected  = " + rowEffected.length);
        log.info("batchInsert (" + users.size() + ") -> Total time in seconds: " + timer.getTotalTimeSeconds() + "  , Row effected: " + rowEffected.length);
    }

    /*
        Reference: https://javabydeveloper.com/spring-jdbctemplate-batch-update-with-maxperformance/

        Key point:
        ===========
        with rewriteBatchedStatements=true the JDBC will pack as many queries as possible into
        a single network packet, lowering this way the network overhead.
        rewriteBatchedStatements=true

        Make sure that "max_allowed_packet" server variable allows max bulk insertion size
        SHOW VARIABLES LIKE 'max_allowed_packet';

        USE the following line in application.properties:
        spring.datasource.jdbc-url=jdbc:mysql://localhost:3306/myapp?rewriteBatchedStatements=true

        to debug JdbcTemplate statements, put the following line in application.properties
        logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG

        IMPORTANT:
        batchSize, number of threads in the thread-pool and max connections in the db connection
        pool should be tuned to get the best performance

        we use thread pool to open multiple connection for bulk insert
    */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchInsertAsync(List<User2> users) throws InterruptedException, ExecutionException {
        StopWatch timer = new StopWatch();

        String sql = "INSERT INTO user2 " +
                "(username, password, firstName, lastName, email, phoneNumber, active, locked, date_of_birth)"
                + " VALUES(?,?,?,?,?,?,?,?,?);";

        final AtomicInteger sublist = new AtomicInteger();

        // need to be tuned
//        @Value("${jdbc.batch_insert_size}")
//        private int batchSize;
        int batchSize = 5000;

        log.info("batchInsertAsync -> start threads...");
        CompletableFuture[] futures = users.stream()
                .collect(Collectors.groupingBy(t -> sublist.getAndIncrement() / batchSize))
                .values()
                .stream()
                .map(ul -> runBatchInsert(ul, sql))
                .toArray(CompletableFuture[]::new);

        CompletableFuture<Void> run = CompletableFuture.allOf(futures);

        log.info("batchInsertAsync -> run operation...");


        timer.start();
        run.get();
        timer.stop();

        log.info("batchInsertAsync (" + users.size() + ") -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    private CompletableFuture<Void> runBatchInsert(List<User2> users, String sql) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.batchUpdate(sql, new UserBatchPreparedStatementSetter(users));
        }, executor);
    }
}
