package com.example.demo;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final IdempotencyService idempotencyService;
    private final OrderCacheService orderCacheService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey != null) {
            var existing = idempotencyService.getExistingResponse(idempotencyKey);
            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(existing.get());
            }
        }

        Order order = new Order();
        order.setProductName(request.productName());
        order.setQuantity(request.quantity());
        order.setPrice(request.price());
        Order saved = orderRepository.save(order);
        OrderResponse response = OrderResponse.from(saved);

        if (idempotencyKey != null) {
            idempotencyService.saveResponse(idempotencyKey, response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
        OrderResponse cached = orderCacheService.getCachedOrder(id);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        OrderResponse response = OrderResponse.from(order);

        orderCacheService.cacheOrder(id, response);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderRepository.findAll().stream()
            .map(OrderResponse::from).toList());
    }
}