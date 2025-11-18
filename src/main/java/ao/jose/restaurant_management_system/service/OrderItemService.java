package ao.jose.restaurant_management_system.service;

import ao.jose.restaurant_management_system.dto.*;
import ao.jose.restaurant_management_system.dto.response.*;
import ao.jose.restaurant_management_system.dto.request.*;
import ao.jose.restaurant_management_system.dto.statistics.*;
import ao.jose.restaurant_management_system.model.*;
import ao.jose.restaurant_management_system.model.enums.*;
import ao.jose.restaurant_management_system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;


    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getAllOrderItems() {
        log.info("Fetching all order items");
        return orderItemRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public OrderItemResponseDTO getOrderItemById(Long id) {
        log.info("Fetching order item with id: {}", id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));
        return mapToDTO(orderItem);
    }


    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getOrderItemsByOrder(Long orderId) {
        log.info("Fetching order items for order id: {}", orderId);
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getOrderItemsByStatus(String status) {
        log.info("Fetching order items with status: {}", status);
        try {
            OrderItemStatus orderItemStatus = OrderItemStatus.valueOf(status.toUpperCase());
            return orderItemRepository.findByStatus(orderItemStatus)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order item status: " + status);
        }
    }


    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getOrderItemsByMenuItem(Long menuItemId) {
        log.info("Fetching order items for menu item id: {}", menuItemId);
        return orderItemRepository.findByMenuItemId(menuItemId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getPendingOrderItems() {
        log.info("Fetching all pending order items");
        return orderItemRepository.findByStatus(OrderItemStatus.PENDING)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<KitchenOrderItemDTO> getKitchenPendingItems() {
        log.info("Fetching pending items for kitchen");
        return orderItemRepository.findPendingKitchenItems()
                .stream()
                .map(this::mapToKitchenDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<KitchenOrderItemDTO> getKitchenPendingItemsByCategory(Long categoryId) {
        log.info("Fetching pending kitchen items for category id: {}", categoryId);
        return orderItemRepository.findPendingItemsByCategory(categoryId)
                .stream()
                .map(this::mapToKitchenDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderItemResponseDTO> getOrderItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching order items between {} and {}", startDate, endDate);
        return orderItemRepository.findByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public OrderItemResponseDTO createOrderItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO) {
        log.info("Creating new order item for order id: {}", orderId);

        // Find order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if order can be modified
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot add items to a paid or cancelled order");
        }

        // Find menu item
        MenuItem menuItem = menuItemRepository.findById(orderItemRequestDTO.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + orderItemRequestDTO.getMenuItemId()));

        // Check if menu item is available
        if (!menuItem.getAvailable()) {
            throw new RuntimeException("Menu item is not available: " + menuItem.getName());
        }

        // Check if item already exists in order (optional - para agrupar)
        List<OrderItem> existingItems = orderItemRepository.findByOrderIdAndMenuItemId(orderId, orderItemRequestDTO.getMenuItemId());
        if (!existingItems.isEmpty() && existingItems.get(0).isPending()) {
            // Se já existe um item pendente igual, atualiza a quantidade
            OrderItem existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + orderItemRequestDTO.getQuantity());
            existingItem.setNotes(orderItemRequestDTO.getNotes());

            OrderItem updatedItem = orderItemRepository.save(existingItem);
            log.info("Order item quantity updated for existing item id: {}", updatedItem.getId());

            // Update order total
            updateOrderTotal(order);

            return mapToDTO(updatedItem);
        }

        // Create new order item
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(orderItemRequestDTO.getQuantity())
                .notes(orderItemRequestDTO.getNotes())
                .status(orderItemRequestDTO.getStatus())
                .build();

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Update order total
        updateOrderTotal(order);

        log.info("Order item created successfully with id: {}", savedOrderItem.getId());
        return mapToDTO(savedOrderItem);
    }


    @Transactional
    public OrderItemResponseDTO updateOrderItem(Long id, OrderItemRequestDTO orderItemRequestDTO) {
        log.info("Updating order item with id: {}", id);

        OrderItem existingOrderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        // Find menu item if changed
        MenuItem menuItem = existingOrderItem.getMenuItem();
        if (!menuItem.getId().equals(orderItemRequestDTO.getMenuItemId())) {
            menuItem = menuItemRepository.findById(orderItemRequestDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + orderItemRequestDTO.getMenuItemId()));

            if (!menuItem.getAvailable()) {
                throw new RuntimeException("Menu item is not available: " + menuItem.getName());
            }
        }

        existingOrderItem.setMenuItem(menuItem);
        existingOrderItem.setQuantity(orderItemRequestDTO.getQuantity());
        existingOrderItem.setNotes(orderItemRequestDTO.getNotes());
        existingOrderItem.setStatus(orderItemRequestDTO.getStatus());

        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);

        // Update order total
        updateOrderTotal(existingOrderItem.getOrder());

        log.info("Order item updated successfully with id: {}", id);
        return mapToDTO(updatedOrderItem);
    }


    @Transactional
    public OrderItemResponseDTO updateOrderItemQuantity(Long id, Integer quantity) {
        log.info("Updating quantity for order item id: {} to {}", id, quantity);

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        orderItem.setQuantity(quantity);
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

        // Update order total
        updateOrderTotal(orderItem.getOrder());

        log.info("Order item quantity updated successfully for id: {}", id);
        return mapToDTO(updatedOrderItem);
    }


    @Transactional
    public OrderItemResponseDTO updateOrderItemStatus(Long id, String status) {
        log.info("Updating status for order item id: {} to {}", id, status);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        try {
            OrderItemStatus newStatus = OrderItemStatus.valueOf(status.toUpperCase());
            orderItem.setStatus(newStatus);

            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
            log.info("Order item status updated successfully for id: {}", id);

            return mapToDTO(updatedOrderItem);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order item status: " + status);
        }
    }


    @Transactional
    public OrderItemResponseDTO updateOrderItemNotes(Long id, String notes) {
        log.info("Updating notes for order item id: {}", id);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        orderItem.setNotes(notes);
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

        log.info("Order item notes updated successfully for id: {}", id);
        return mapToDTO(updatedOrderItem);
    }


    @Transactional
    public OrderItemResponseDTO markAsCompleted(Long id) {
        log.info("Marking order item as completed for id: {}", id);
        return updateOrderItemStatus(id, "COMPLETED");
    }


    @Transactional
    public OrderItemResponseDTO markAsPending(Long id) {
        log.info("Marking order item as pending for id: {}", id);
        return updateOrderItemStatus(id, "PENDING");
    }


    @Transactional
    public OrderItemResponseDTO markAsFailed(Long id) {
        log.info("Marking order item as failed for id: {}", id);
        return updateOrderItemStatus(id, "FAILED");
    }


    @Transactional
    public void deleteOrderItem(Long id) {
        log.info("Deleting order item with id: {}", id);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));

        Order order = orderItem.getOrder();

        orderItemRepository.delete(orderItem);

        // Update order total
        updateOrderTotal(order);

        log.info("Order item deleted successfully with id: {}", id);
    }


    @Transactional
    public void deleteOrderItemsByOrder(Long orderId) {
        log.info("Deleting all order items for order id: {}", orderId);
        orderItemRepository.deleteByOrderId(orderId);
        log.info("All order items deleted for order id: {}", orderId);
    }


    @Transactional(readOnly = true)
    public OrderItemStatisticsDTO getOrderItemStatistics() {
        log.info("Fetching order item statistics");

        Long totalOrderItems = orderItemRepository.count();
        Long pendingOrderItems = orderItemRepository.countPendingOrderItems() != null ?
                orderItemRepository.countPendingOrderItems() : 0L;
        Long completedOrderItems = orderItemRepository.countCompletedItemsSince(
                LocalDateTime.now().withYear(1970)) != null ?
                orderItemRepository.countCompletedItemsSince(LocalDateTime.now().withYear(1970)) : 0L;
        Long todayCompletedItems = orderItemRepository.countCompletedItemsSince(
                LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)) != null ?
                orderItemRepository.countCompletedItemsSince(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)) : 0L;

        // Calcular média de itens por pedido (simplificado)
        long totalOrders = orderRepository.count();
        Double averageItemsPerOrder = totalOrders > 0 ?
                (double) totalOrderItems / totalOrders : 0.0;

        return new OrderItemStatisticsDTO(
                totalOrderItems, pendingOrderItems, completedOrderItems,
                todayCompletedItems, averageItemsPerOrder
        );
    }


    @Transactional(readOnly = true)
    public List<MenuItemSalesDTO> getPopularMenuItems(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching popular menu items between {} and {}", startDate, endDate);

        if (startDate == null) startDate = LocalDateTime.now().minusMonths(1);
        if (endDate == null) endDate = LocalDateTime.now();

        return orderItemRepository.findPopularMenuItems(startDate, endDate)
                .stream()
                .map(obj -> {
                    MenuItem menuItem = (MenuItem) obj[0];
                    Long totalSold = (Long) obj[1];
                    BigDecimal totalRevenue = menuItem.getPrice().multiply(BigDecimal.valueOf(totalSold));

                    return new MenuItemSalesDTO(
                            menuItem.getId(),
                            menuItem.getName(),
                            menuItem.getCategory().getName(),
                            totalSold,
                            totalRevenue
                    );
                })
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CategoryOrderCountDTO> getOrderItemsByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching order items count by category between {} and {}", startDate, endDate);

        if (startDate == null) startDate = LocalDateTime.now().minusMonths(1);
        if (endDate == null) endDate = LocalDateTime.now();

        return orderItemRepository.countOrderItemsByCategory(startDate, endDate)
                .stream()
                .map(obj -> new CategoryOrderCountDTO(
                        (String) obj[0],
                        (Long) obj[1]
                ))
                .collect(Collectors.toList());
    }

    private void updateOrderTotal(Order order) {
        BigDecimal newTotal = order.getOrderItems().stream()
                .map(item -> item.getMenuItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(newTotal);
        orderRepository.save(order);
    }

    private OrderItemResponseDTO mapToDTO(OrderItem orderItem) {
        MenuItemResponseDTO menuItemDTO = MenuItemResponseDTO.builder()
                .id(orderItem.getMenuItem().getId())
                .name(orderItem.getMenuItem().getName())
                .description(orderItem.getMenuItem().getDescription())
                .price(orderItem.getMenuItem().getPrice())
                .category(CategoryResponseDTO.builder()
                        .id(orderItem.getMenuItem().getCategory().getId())
                        .name(orderItem.getMenuItem().getCategory().getName())
                        .build())
                .preparationTime(orderItem.getMenuItem().getPreparationTime())
                .available(orderItem.getMenuItem().getAvailable())
                .build();

        BigDecimal subtotal = orderItem.getMenuItem().getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .menuItem(menuItemDTO)
                .quantity(orderItem.getQuantity())
                .notes(orderItem.getNotes())
                .status(orderItem.getStatus())
                .subtotal(subtotal)
                .createdAt(orderItem.getCreatedAt())
                .build();
    }

    private KitchenOrderItemDTO mapToKitchenDTO(OrderItem orderItem) {
        return KitchenOrderItemDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .tableNumber(orderItem.getOrder().getTable().getTableNumber())
                .customerName(orderItem.getOrder().getCustomerName())
                .menuItemName(orderItem.getMenuItem().getName())
                .quantity(orderItem.getQuantity())
                .notes(orderItem.getNotes())
                .status(orderItem.getStatus())
                .preparationTime(orderItem.getMenuItem().getPreparationTime())
                .createdAt(orderItem.getCreatedAt())
                .orderCreatedAt(orderItem.getOrder().getCreatedAt())
                .categoryName(orderItem.getMenuItem().getCategory().getName())
                .specialInstructions(orderItem.getNotes())
                .build();
    }
}