package edu.lysak.kafkastreams.streamingAnalytics.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SalesOrder {

    private int orderId;
    private String product;
    private int quantity;
    private double price;

}
