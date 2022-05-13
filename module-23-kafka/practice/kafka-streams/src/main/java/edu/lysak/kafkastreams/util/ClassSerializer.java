package edu.lysak.kafkastreams.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@NoArgsConstructor
public class ClassSerializer<T> implements Serializer<T> {

    private static final Gson GSON =
            new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, T type) {
        return GSON.toJson(type).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
    }

}
