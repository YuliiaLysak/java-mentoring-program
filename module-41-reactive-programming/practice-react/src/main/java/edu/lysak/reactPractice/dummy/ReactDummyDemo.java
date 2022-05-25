package edu.lysak.reactPractice.dummy;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class ReactDummyDemo {
    public static void main(String[] args) throws InterruptedException {
        Mono.empty();
        Flux.empty();
        Mono<Integer> mono = Mono.just(1);
        Flux<Integer> flux = Flux.just(1, 2, 3);

        Flux<Integer> fluxFromMono = mono.flux();
        Mono<Boolean> monoFromFlux = flux.any(s -> s.equals(1));
        Mono<Integer> integerMono = flux.elementAt(1);

        Flux.range(1, 5);
        Flux.fromIterable(List.of(1, 2, 3)).subscribe(System.out::println);

        Flux
                .<String>generate(sink -> {
                    sink.next("hello");
                })
                .delayElements(Duration.ofMillis(500))
                .take(4)
                .subscribe(System.out::println);

        Thread.sleep(4000L);
    }
}
