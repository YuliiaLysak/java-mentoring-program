package edu.lysak.events.controller;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

final class EventServiceControllerTestData {
    private EventServiceControllerTestData() {
    }

    public static class EventRequestMandatoryField implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.arguments(null, "place", "speaker", "event type"),
                    Arguments.arguments("title", null, "speaker", "event type"),
                    Arguments.arguments("title", "place", null, "event type"),
                    Arguments.arguments("title", "place", "speaker", null),
                    Arguments.arguments(null, null, null, null)
            );
        }
    }
}
