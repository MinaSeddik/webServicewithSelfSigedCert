package com.example.springbootproject.repository;


import com.example.springbootproject.model.BankAccount;
import io.r2dbc.client.R2dbc;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ReactiveAtmRepository implements InitializingBean {

    @Autowired
    private ConnectionFactory connectionFactory;

    private R2dbc r2dbc;

    @Override
    public void afterPropertiesSet() throws Exception {
        r2dbc = new R2dbc(connectionFactory);
    }

    public Flux<BankAccount> findAllBankAccounts() {

        String selectSql = "SELECT * FROM atm";

        return Mono.from(connectionFactory.create())
                .flatMapMany(
                        c -> Flux.from(c.createStatement(selectSql)
                                .execute())
                )
                .log()
                .flatMap(result -> result
                        .map((row, rowMetadata) -> {

                            rowMetadata.getColumnMetadatas()
                                    .forEach(columnMetadata -> log.info("column name:{}, type: {}", columnMetadata.getName(), columnMetadata.getJavaType()));

                            Integer id = row.get("id", Integer.class);
                            String firstName = row.get("first_name", String.class);
                            Integer balance = row.get("balance", Integer.class);
                            Integer branchId = row.get("branch_id", Integer.class);

                            return BankAccount.builder()
                                    .firstName(firstName)
                                    .balance(balance)
                                    .branchId(branchId)
                                    .id(id)
                                    .build();
                        }))
                .doOnNext(data -> log.info(": {}", data));

    }

    public Mono<BankAccount> findBankAccountByName(String firstNameParam) {

        String selectSql = "SELECT * FROM atm WHERE first_name=?";

        return Mono.from(connectionFactory.create())
                .flatMapMany(c -> Flux.from(
                        c.createStatement(selectSql)
                                .bind(0, firstNameParam)
                                .execute())
                )
                .log()
                .flatMap(result -> result
                        .map((row, rowMetadata) -> {

                            rowMetadata.getColumnMetadatas()
                                    .forEach(columnMetadata -> log.info("column name:{}, type: {}", columnMetadata.getName(), columnMetadata.getJavaType()));

                            Integer id = row.get("id", Integer.class);
                            String firstName = row.get("first_name", String.class);
                            Integer balance = row.get("balance", Integer.class);
                            Integer branchId = row.get("branch_id", Integer.class);

                            return BankAccount.builder()
                                    .firstName(firstName)
                                    .balance(balance)
                                    .branchId(branchId)
                                    .id(id)
                                    .build();
                        })).next()
                .doOnNext(data -> log.info(": {}", data));

    }

    public Flux<String> addBankBranchWithFailure() {

        return r2dbc.inTransaction(handle ->
                        handle.execute("INSERT INTO bank VALUES (?, ?)", 2, "Morgan Stanley")
                                .doOnNext(n -> log.info("{} rows inserted", n))
                                .then(Mono.error(new RuntimeException("something goes wrong!")))) // to fail the insertion

                // here we create a new transaction
                .thenMany(r2dbc.inTransaction(handle -> handle.select("SELECT name FROM bank")
                        .mapResult(result -> result.map((row, rowMetadata) -> row.get("name", String.class)))));

    }

    public Flux<String> addBankAccountWithNewBranch() {

        return r2dbc.inTransaction(handle ->
                        handle.execute("INSERT INTO bank VALUES (?, ?)", 2, "Morgan Stanley")
                                .doOnNext(n -> log.info("{} rows inserted in bank table.", n))
                                .thenMany(handle.execute("INSERT INTO atm VALUES (?, ?, ?, ?)", 3,"Magy", 500, 2))
                                .doOnNext(n -> log.info("{} rows inserted in atm table.", n)))
                // here we create a new transaction
                .thenMany(r2dbc.inTransaction(handle -> handle.select("SELECT first_name FROM atm")
                        .mapResult(result -> result.map((row, rowMetadata) -> row.get("first_name", String.class)))));

    }

    public Flux<String> addBankAccountWithTransaction(Flux<String> process) {

        return r2dbc.inTransaction(handle ->
                        handle.execute("INSERT INTO bank VALUES (?, ?)", 4, "Al-Ahly Bank")
                                .doOnNext(n -> log.info("{} rows inserted in bank table.", n))
                                .thenMany(process))
                // here we create a new transaction
                .thenMany(r2dbc.inTransaction(handle -> handle.select("SELECT name FROM bank")
                        .mapResult(result -> result.map((row, rowMetadata) -> row.get("name", String.class)))));

    }
}
