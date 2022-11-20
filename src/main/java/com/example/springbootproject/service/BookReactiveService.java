package com.example.springbootproject.service;

import com.example.springbootproject.model.Book;
import com.example.springbootproject.repository.impl.ReactiveSpringDataBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static java.time.Duration.between;
import static java.time.Instant.now;
import static reactor.function.TupleUtils.function;


@Service
@Slf4j
public class BookReactiveService {

    @Autowired
    private ReactiveSpringDataBookRepository rxBookRepository;


    public Flux<Book> dosomethingAsFlux() {

        return dosomething().repeat(10);
    }


    public Mono<Book> dosomething() {

        Instant start = now(); // (1)
        Mono<String> title = Mono.delay(Duration.ofSeconds(1)) // (2)
                .thenReturn("Artemis") //
                .doOnSubscribe(s -> log.info("Subscribed for title")) //
                .doOnNext(t -> log.info("Book title resolved: {}", t)); // (2.1)

//        Mono<String> title = Mono.just("Artemis")
//                .doOnSubscribe(s -> log.info("Subscribed for title")) //
//                .doOnNext(t -> log.info("Book title resolved: {}" , t)); // (2.1)


        Mono<Integer> publishingYear = Mono.delay(Duration.ofSeconds(2)) // (3)
                .thenReturn(2017)
                .doOnSubscribe(s -> log.info("Subscribed for publishing year")) //
                .doOnNext(t -> log.info("New publishing year resolved: {}", t)); // (3.1)


        return updatedBookYearByTitle(title, publishingYear) // (4)
                .doOnNext(b -> log.info("Publishing year updated for book: {}", b)) // (4.1)
//                .hasElement() // (4.2)
//                .doOnSuccess(status -> log.info("Updated finished {}, took: {}", status ? "successfully" : "unsuccessfully", between(start, now())));
                .doOnSuccess(book -> log.info("Updated finished {}, took: {}", !Objects.isNull(book) ? "successfully" : "unsuccessfully", between(start, now())));

    }

    public Mono<Book> updatedBookYearByTitle() {

        Instant start = now(); // (1)
        Mono<String> title = Mono.delay(Duration.ofSeconds(1)) // (2)
                .thenReturn("Artemis") //
                .doOnSubscribe(s -> log.info("Subscribed for title")) //
                .doOnNext(t -> log.info("Book title resolved: {}", t)); // (2.1)

//        Mono<String> title = Mono.just("Artemis")
//                .doOnSubscribe(s -> log.info("Subscribed for title")) //
//                .doOnNext(t -> log.info("Book title resolved: {}" , t)); // (2.1)


        Mono<Integer> publishingYear = Mono.delay(Duration.ofSeconds(2)) // (3)
                .thenReturn(2017)
                .doOnSubscribe(s -> log.info("Subscribed for publishing year")) //
                .doOnNext(t -> log.info("New publishing year resolved: {}", t)); // (3.1)

        return updatedBookYearByTitle(title, publishingYear);
    }


    private Mono<Book> updatedBookYearByTitle(Mono<String> title, Mono<Integer> newPublishingYear) {
        return Mono.zip(newPublishingYear, rxBookRepository.findOneByTitle(title))
                .flatMap(function((yearValue, bookValue) -> { // (2)
                    bookValue.setYear(yearValue); //
                    return rxBookRepository.save(bookValue); // (2.1)
                }));
    }


    private Mono<Book> updatedBookYearByTitle2(Mono<String> title, Mono<Integer> newPublishingYear) {

        return rxBookRepository.findOneByTitle(title)
                .doOnNext(book -> log.info("findOneByTitle:: {}", book))
                .flatMap(book -> newPublishingYear
                        .flatMap(year -> { // (3)
                            book.setYear(year);
                            return rxBookRepository.save(book); // (5)
                        }))
                .doOnNext(book -> log.info("flatMap new publish year:: {}", book));
    }
}
