package com.example.springbootproject.repository.batchinsertupdate;

import com.example.springbootproject.domain.User2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class BatchUpdateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    /*
        1. Not a good solution
        2. static SQL builder risks SQL injection
        3. this example is for demonstration only but not advised for production implementation
        4. the time elapsed is still very big compared to insertion time elapsed
        5. BETTER solution is to use insert with ON DUPLICATE KEY UPDATE
        USE: batchUpdateAsync function below
        Reference: https://stackoverflow.com/questions/35726910/bulk-update-mysql-with-where-statement

     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchUpdateStatic(List<User2> users) throws InterruptedException, ExecutionException {
        StopWatch timer = new StopWatch();

        final AtomicInteger sublists = new AtomicInteger();

        // need to be tuned
//        @Value("${jdbc.batch_insert_size}")
//        private int batchSize;
        int batchSize = 5000;

        String[] queries = users.stream()
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / batchSize))
                .values()
                .stream()
                .map(UserBatchQueryBuilder::buildUpdateQuery)
                .toArray(String[]::new);

        timer.start();
        log.info("Queries: " + Arrays.asList(queries));
        jdbcTemplate.batchUpdate(queries);   // we can parallelize those queries using executor thread pool
        timer.stop();

        log.info("batchUpdateStatic -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }


    /*

        Reference: https://stackoverflow.com/questions/35726910/bulk-update-mysql-with-where-statement
        INSERT into `table` (id, fruit)
        VALUES (1, 'apple'), (2, 'orange'), (3, 'peach')
        ON DUPLICATE KEY UPDATE fruit = VALUES(fruit);


        BOUNCE:
        ---------
        Ref: https://stackoverflow.com/questions/23505349/only-update-changed-values-for-on-duplicate-key-update
        INSERT INTO admin_sites2 (site_id,created_at,active)
            VALUES ('$key',now(),'$value')
            ON DUPLICATE KEY
            UPDATE updated_at = IF(active=VALUES(active),updated_at,NOW()),
            active = VALUES(active)

     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchUpdateAsync(List<User2> users) throws InterruptedException, ExecutionException {
        StopWatch timer = new StopWatch();

        String sql = "INSERT INTO user2 (id, username, password, firstName, lastName, email, phoneNumber, active, locked, date_of_birth) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE username = VALUES(username), password = VALUES(password), firstName = VALUES(firstName), " +
                "lastName = VALUES(lastName), email = VALUES(email), phoneNumber = VALUES(phoneNumber), " +
                "active = VALUES(active), locked = VALUES(locked), date_of_birth = VALUES(date_of_birth);";

        final AtomicInteger sublist = new AtomicInteger();

        // need to be tuned
//        @Value("${jdbc.batch_insert_size}")
//        private int batchSize;
        int batchSize = 5000;

        log.info("batchUpdateAsync -> start threads...");
        CompletableFuture[] futures = users.stream()
                .collect(Collectors.groupingBy(t -> sublist.getAndIncrement() / batchSize))
                .values()
                .stream()
                .map(ul -> runBatchUpdate(ul, sql))
                .toArray(CompletableFuture[]::new);

        CompletableFuture<Void> run = CompletableFuture.allOf(futures);

        log.info("batchUpdateAsync -> run operation...");

        timer.start();
        run.get();
        timer.stop();

        log.info("batchUpdateAsync (" + users.size() + ") -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    private CompletableFuture<Void> runBatchUpdate(List<User2> users, String sql) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.batchUpdate(sql, new UserBatchUpdatePreparedStatementSetter(users));
        }, executor);
    }

}
