package edu.lysak.clientapp.controller;

import edu.lysak.clientapp.service.OrderService;
import edu.lysak.domain.models.OrderInfo;
import edu.lysak.domain.models.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderInfo orderInfo) {
        return ResponseEntity.ok(orderService.processOrder(orderInfo));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<Status> getOrderStatus(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }
}
