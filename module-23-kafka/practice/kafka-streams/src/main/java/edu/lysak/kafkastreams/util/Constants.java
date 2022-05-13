package edu.lysak.kafkastreams.util;

public final class Constants {
    private Constants() {}



    public static final String SERVER = "localhost:9092";
    public static final String ORDERS_INPUT_TOPIC = "streaming.orders.input";
    public static final String ALERTS_INPUT_TOPIC = "streaming.alerts.input";
    public static final String ALERTS_CRITICAL_TOPIC = "streaming.alerts.critical";
    public static final String ALERTS_HIGHVOLUME_TOPIC = "streaming.alerts.highvolume";
    public static final String LEADERBOARDS_INPUT_TOPIC = "streaming.leaderboards.input";
    public static final String SENTIMENT_INPUT_TOPIC = "streaming.sentiment.input";
    public static final String VIEWS_INPUT_TOPIC = "streaming.views.input";
}
