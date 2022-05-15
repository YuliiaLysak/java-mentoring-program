package edu.lysak.clientapp.service;

import edu.lysak.clientapp.domain.Order;
import edu.lysak.clientapp.exception.NotFoundException;
import edu.lysak.clientapp.repository.OrderRepository;
import edu.lysak.domain.models.OrderInfo;
import edu.lysak.domain.models.PizzaSize;
import edu.lysak.domain.models.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaService kafkaService;

    public OrderService(OrderRepository orderRepository, KafkaService kafkaService) {
        this.orderRepository = orderRepository;
        this.kafkaService = kafkaService;
    }

    public Long processOrder(OrderInfo orderInfo) {
        Order order = mapToOrder(orderInfo);
        orderRepository.save(order);

        Long orderId = order.getOrderId();
        orderInfo.setOrderId(orderId);
        orderInfo.setStatus(Status.ORDER_PLACED);
        kafkaService.sendOrder(orderId, orderInfo);
        return orderId;
    }

    private Order mapToOrder(OrderInfo orderInfo) {
        String size = orderInfo.getPizzaSize();
        Optional<PizzaSize> pizzaSize = PizzaSize.getByDescription(size);
        if (pizzaSize.isEmpty()) {
            log.warn("Pizza size '{}' is not found", size);
            throw new NotFoundException(String.format("Pizza size '%s' is not found", size));
        }
        return Order.builder()
                .orderTime(LocalDateTime.now())
                .pizzaSize(pizzaSize.get())
                .toppings(orderInfo.getToppings())
                .finalPrice(pizzaSize.get().getPrice())
                .status(Status.ORDER_PLACED)
                .build();
    }

    public Status getOrderStatus(Long orderId) {
        return orderRepository
                .findOrderStatusById(orderId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Order with id=%s not found", orderId)));
    }
}
