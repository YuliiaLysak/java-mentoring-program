package edu.lysak.clientapp.domain;

import edu.lysak.domain.models.PizzaSize;
import edu.lysak.domain.models.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "orders")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "pizza_size")
    private PizzaSize pizzaSize;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "pizza_toppings", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "topping_name")
    private Set<String> toppings;

    @Column(name = "final_price")
    private Double finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
