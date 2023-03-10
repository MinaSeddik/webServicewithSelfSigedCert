package com.example.springbootproject.service;

import com.example.springbootproject.domain.Temperature;
import io.reactivex.Observable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component //
public class TemperatureSensor {
    private final Random random = new Random();
    private final Observable<Temperature> dataStream =
            Observable.range(0, Integer.MAX_VALUE)
                    .concatMap(tick -> Observable
                            .just(tick)
                            .delay(random.nextInt(5000), TimeUnit.MILLISECONDS)
                            .map(tickValue -> this.probe()))
                    .publish()
                    .refCount();


    private final Flux<Temperature> fluxStream =
            Flux.range(0, Integer.MAX_VALUE)
                    .concatMap(tick -> Flux
                            .just(tick)
                            .delayElements(Duration.ofMillis(random.nextInt(5000)))
                            .map(tickValue -> this.probe()))
                    .publish()
                    .refCount();

    private Temperature probe() {
        return new Temperature(16 + random.nextGaussian() * 10);
    }

    public Flux<Temperature> temperatureStream() {
        return fluxStream;
    }
}