package edu.lysak.courier.service;

import edu.lysak.domain.models.Status;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

final class KafkaServiceTestData {
    private KafkaServiceTestData() {
    }

    public static class ValidOrderStatus implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.arguments(Status.PREPARED, Status.DELIVERING),
                    Arguments.arguments(Status.DELIVERING, Status.COMPLETED)
            );
        }
    }

    public static class IgnoredOrderStatus implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(Status.ORDER_PLACED),
                    Arguments.of(Status.PREPARING),
                    Arguments.of(Status.COMPLETED),
                    Arguments.of(Status.CANCELED)
            );
        }
    }
}
