package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Temperature;
import com.example.springbootproject.service.TemperatureSensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class ReactiveTemperatureEmitterController {

    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
    @Autowired
    private TemperatureSensor temperatureSensor;

    @Autowired
    private WebClient webClient;


    @RequestMapping(value = "/temperature-stream", method = RequestMethod.GET)
    public SseEmitter events(HttpServletRequest request, HttpServletResponse response) {

        SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);
        Disposable disposable = temperatureSensor.temperatureStream()
                .subscribeOn(Schedulers.parallel())
                .subscribe(item -> sendDataItem(emitter, item), this::handleError);
//                .subscribe(emitter::send, this::handleError);

        emitter.onCompletion(disposable::dispose);
        emitter.onTimeout(disposable::dispose);

        return emitter;
    }

    @RequestMapping(value = "/temperature-test-stream", method = RequestMethod.GET)
    public void testEvents() throws InterruptedException {

        webClient.get()
                .uri("/temperature-stream")
                .retrieve()
                .bodyToFlux(Temperature.class)
                .subscribe(t -> log.info("Temperature received: {}", t));

        TimeUnit.MINUTES.sleep(3);

    }

    @RequestMapping(value = "/temperature-test-stream2", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Temperature> testEvents2() {

        Flux<Temperature> temperatureFlux = webClient.get()
                .uri("/temperature-stream")
                .retrieve()
                .bodyToFlux(Temperature.class)
                .take(5);

        return temperatureFlux;
    }

    //https://stackoverflow.com/questions/52098863/whats-the-difference-between-text-event-stream-and-application-streamjson

    @RequestMapping(value = "/temperature-test-stream3", method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Temperature> testEvents3() {

        return temperatureSensor.temperatureStream()
                .doOnNext(t -> log.info("Sending Temperature: {}", t));
    }

    @RequestMapping(value = "/temperature-test-stream4", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Temperature> testEvents4() {

        return temperatureSensor.temperatureStream()
                .doOnNext(t -> log.info("Sending Temperature: {}", t));
    }

    @RequestMapping(value = "/temperature-test-stream5", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Temperature> testEvents5() {

        return temperatureSensor.temperatureStream()
                .doOnNext(t -> log.info("Sending Temperature: {}", t));
    }
    private void handleError(Throwable throwable) {
        log.error("{}: ", throwable.getClass(), throwable);
    }

    private void sendDataItem(SseEmitter emitter, Object object) {
        log.info("Sending {}: ", object);
        try {
            emitter.send(object);
        } catch (IOException e) {
            log.error("{}: ", e.getClass(), e);
        }
    }
}