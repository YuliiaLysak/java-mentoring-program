package edu.lysak.reactPractice.dummy;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class LearnMonoMain {
    public static void main(String[] args) {
        System.out.println("Start");

        getOrSaveFlow("new world");
        getOrSaveFlow("new world");

        System.out.println("End");
    }

    private static void streamMonoMakeAnAction() {
        Stream.of("Hello")
                .forEach(it -> {
                    // it -> Entity
                    // repository.save(Entity)
                });

        Mono.just("Hello")
                .subscribe(it -> {
                    // it -> Entity
                    // repository.save(Entity)
                });
    }

    private static void streamMonoFlux() {
        Stream.of("Stream: Hello", "Stream: World")
                .map(String::length)
                .forEach(System.out::println);

        Mono.just("Mono: Hello")
                .map(String::length)
                .subscribe(System.out::println);

        Flux.just("Flux: Hello", "Flux: World")
                .map(String::length)
                .subscribe(System.out::println);
    }

    private static void streamMonoFluxMapFlatMap() {
        Stream.of("Stream: Hello", "Stream: World")
                .map(String::codePoints)
//                .map(new Function<IntStream, Object>() {
//                    @Override
//                    public Object apply(IntStream intStream) {
//                        return null;
//                    }
//                })
//                .flatMap(new Function<IntStream, Stream<?>>() {
//                    @Override
//                    public Stream<?> apply(IntStream intStream) {
//                        return null;
//                    }
//                })
                .forEach(System.out::println);

        Mono<String> just = Mono.just("Mono: Hello");
        Mono<Integer> mapLength = just.map(String::length);
        Mono<Double> doubleMono = mapLength.flatMap(it -> Mono.just(it.doubleValue()));
        doubleMono
//                .map(new Function<Integer, Object>() {
//                    @Override
//                    public Object apply(Integer integer) {
//                        return null;
//                    }
//                })
//                .flatMap(new Function<Integer, Mono<Double>>() {
//                    @Override
//                    public Mono<Double> apply(Integer integer) {
//                        return null;
//                    }
//                })
                .subscribe(System.out::println);

        Flux.just("Flux: Hello", "Flux: World")
                .map(String::length)
                .subscribe(System.out::println);
    }

    private static void streamMonoFluxEmpty() {
        System.out.println("Empty:");

        Stream.empty()
                .forEach(System.out::println);
        Flux.empty()
                .subscribe(System.out::println);
        Mono.empty()
                .subscribe(System.out::println);
    }

    private static final Set<String> WORDS = new HashSet<>(Set.of("hello", "world"));

    private static void getOrSaveFlow(String word) {
        find(word)
                .flatMap(it -> Mono.error(new IllegalArgumentException("Word already exist")))
                .onErrorResume(err -> add(word))
//                .onErrorMap(error -> new RuntimeException("Map To Another Error", error))
//                .onErrorReturn(RuntimeException.class, "Brrrr")
                .subscribe(System.out::println, System.out::println);
    }

    private static Mono<String> find(String word) {
        Optional<String> wordFromSet = WORDS.stream()
                .filter(it -> it.equals(word))
                .findFirst();
        return wordFromSet
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new IllegalArgumentException("Unknown word: '" + word + "'")));
    }

    private static Mono<Boolean> add(String word) {
       return Mono.just(WORDS.add(word));
    }
}
