package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.Book;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ReactiveSpringDataBookRepository {
    public Mono<Book> findOneByTitle(Mono<String> title) {

        Book book = Book.builder().build();
        title.doOnNext(t -> log.info("**title: {}", t))
                .subscribe(book::setTitle);

        log.info("here is the book {}", book);

        return Mono.just(book);
    }

    public Mono<Book> save(Book book) {

        return Mono.just(book);
    }

    @Autowired
    private ConnectionFactory connectionFactory;

    public void trytest() {

        String selectSql = "SELECT * FROM persons WHERE first_name=$1";

        Mono.from(connectionFactory.create())
                .flatMapMany(
                        c -> Flux.from(c.createStatement(selectSql)
                                .bind("$1", "Hantsy")
                                .execute())
                );

    }
//        ConnectionFactory connectionFactory = null;
//        DatabaseClient databaseClient = DatabaseClient.create(connectionFactory);
//
//        Mono<Integer> count = databaseClient.execute()
//                .sql(
//                        "INSERT INTO legoset (id, name, manual) " +
//                                "VALUES($1, $2, $3)")
//                .bind("$1", 42055)
//                .bind("$2", "Description")
//                .bindNull("$3", Integer.class)
//                .fetch()
//                .rowsUpdated();
//
//        Flux<Map<String, Object>> rows = databaseClient.execute()
//                .sql("SELECT id, name, manual FROM legoset")
//                .fetch()
//                .all();
//    }
}
