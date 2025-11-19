package ao.jose.restaurant_management_system.controller;

import ao.jose.restaurant_management_system.dto.CategoryOrderCountDTO;
import ao.jose.restaurant_management_system.dto.MenuItemSalesDTO;
import ao.jose.restaurant_management_system.dto.request.OrderItemRequestDTO;
import ao.jose.restaurant_management_system.dto.response.OrderItemResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.KitchenOrderItemDTO;
import ao.jose.restaurant_management_system.dto.statistics.OrderItemStatisticsDTO;
import ao.jose.restaurant_management_system.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemResponseDTO>> getAllOrderItems(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long menuItemId) {

        List<OrderItemResponseDTO> orderItems;

        if (status != null) {
            orderItems = orderItemService.getOrderItemsByStatus(status);
        } else if (orderId != null) {
            orderItems = orderItemService.getOrderItemsByOrder(orderId);
        } else if (menuItemId != null) {
            orderItems = orderItemService.getOrderItemsByMenuItem(menuItemId);
        } else {
            orderItems = orderItemService.getAllOrderItems();
        }

        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OrderItemResponseDTO>> getPendingOrderItems() {
        List<OrderItemResponseDTO> orderItems = orderItemService.getPendingOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/kitchen/pending")
    public ResponseEntity<List<KitchenOrderItemDTO>> getKitchenPendingItems(
            @RequestParam(required = false) Long categoryId) {

        List<KitchenOrderItemDTO> kitchenItems = (categoryId != null) ?
                orderItemService.getKitchenPendingItemsByCategory(categoryId) :
                orderItemService.getKitchenPendingItems();

        return ResponseEntity.ok(kitchenItems);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<OrderItemResponseDTO>> getOrderItemsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<OrderItemResponseDTO> orderItems = orderItemService.getOrderItemsByDateRange(startDate, endDate);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponseDTO> getOrderItemById(@PathVariable Long id) {
        OrderItemResponseDTO orderItem = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItem);
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<OrderItemResponseDTO> createOrderItem(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemRequestDTO orderItemRequestDTO) {

        OrderItemResponseDTO createdOrderItem = orderItemService.createOrderItem(orderId, orderItemRequestDTO);
        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItem(
            @PathVariable Long id,
            @RequestBody OrderItemRequestDTO orderItemRequestDTO) {

        OrderItemResponseDTO updatedOrderItem = orderItemService.updateOrderItem(id, orderItemRequestDTO);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItemQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        OrderItemResponseDTO updatedOrderItem = orderItemService.updateOrderItemQuantity(id, quantity);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItemStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        OrderItemResponseDTO updatedOrderItem = orderItemService.updateOrderItemStatus(id, status);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @PatchMapping("/{id}/notes")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItemNotes(
            @PathVariable Long id,
            @RequestParam String notes) {

        OrderItemResponseDTO updatedOrderItem = orderItemService.updateOrderItemNotes(id, notes);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<OrderItemResponseDTO> markAsCompleted(@PathVariable Long id) {
        OrderItemResponseDTO completedOrderItem = orderItemService.markAsCompleted(id);
        return ResponseEntity.ok(completedOrderItem);
    }

    @PostMapping("/{id}/pending")
    public ResponseEntity<OrderItemResponseDTO> markAsPending(@PathVariable Long id) {
        OrderItemResponseDTO pendingOrderItem = orderItemService.markAsPending(id);
        return ResponseEntity.ok(pendingOrderItem);
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<OrderItemResponseDTO> markAsFailed(@PathVariable Long id) {
        OrderItemResponseDTO failedOrderItem = orderItemService.markAsFailed(id);
        return ResponseEntity.ok(failedOrderItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> deleteOrderItemsByOrder(@PathVariable Long orderId) {
        orderItemService.deleteOrderItemsByOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderItemStatisticsDTO> getOrderItemStatistics() {
        OrderItemStatisticsDTO statistics = orderItemService.getOrderItemStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/popular-items")
    public ResponseEntity<List<MenuItemSalesDTO>> getPopularMenuItems(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<MenuItemSalesDTO> popularItems = orderItemService.getPopularMenuItems(startDate, endDate);
        return ResponseEntity.ok(popularItems);
    }

    @GetMapping("/category-stats")
    public ResponseEntity<List<CategoryOrderCountDTO>> getOrderItemsByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<CategoryOrderCountDTO> categoryStats = orderItemService.getOrderItemsByCategory(startDate, endDate);
        return ResponseEntity.ok(categoryStats);
    }
}