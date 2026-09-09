package ao.jose.restaurant_management_system.controller;

import ao.jose.restaurant_management_system.dto.request.OrderItemRequestDTO;
import ao.jose.restaurant_management_system.dto.request.OrderRequestDTO;
import ao.jose.restaurant_management_system.dto.response.OrderResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.OrderStatisticsDTO;
import ao.jose.restaurant_management_system.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long tableId,
            @RequestParam(required = false) String customerName) {

        List<OrderResponseDTO> orders;

        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else if (tableId != null) {
            orders = orderService.getOrdersByTable(tableId);
        } else if (customerName != null) {
            orders = orderService.getOrdersByCustomerName(customerName);
        } else {
            orders = orderService.getAllOrders();
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrderResponseDTO>> getActiveOrders() {
        List<OrderResponseDTO> orders = orderService.getActiveOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder( @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO createdOrder = orderService.createOrder(orderRequestDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponseDTO> addItemToOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemRequestDTO orderItemRequestDTO) {

        OrderResponseDTO updatedOrder = orderService.addItemToOrder(id, orderItemRequestDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<OrderResponseDTO> updateOrderItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId,
            @RequestParam Integer quantity) {

        OrderResponseDTO updatedOrder = orderService.updateOrderItemQuantity(orderId, orderItemId, quantity);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<OrderResponseDTO> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {

        OrderResponseDTO updatedOrder = orderService.removeItemFromOrder(orderId, orderItemId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        OrderResponseDTO cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderStatisticsDTO> getOrderStatistics() {
        OrderStatisticsDTO statistics = orderService.getOrderStatistics();
        return ResponseEntity.ok(statistics);
    }
}