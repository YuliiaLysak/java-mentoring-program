package edu.lysak.kafkastreams.streamingAnalytics.service;

//Aggregate orders by Window and Product
public class OrderAggregator {

    //Initialize total Value
    private Double totalValue = 0.0;

    //Return current value
    public Double getTotalValue() {
        return totalValue;
    }

    //Add to total value
    public OrderAggregator add(Double value) {
        totalValue += value;
        return this;
    }

}
