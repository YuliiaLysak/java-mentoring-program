package edu.lysak.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {

    private Long orderId;
    private String pizzaSize;
    private Set<String> toppings;
    private Status status;
}
