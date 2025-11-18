package ao.jose.restaurant_management_system.service;

import ao.jose.restaurant_management_system.dto.request.*;
import ao.jose.restaurant_management_system.dto.response.*;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToDTO(order);
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        log.info("Fetching orders with status: {}", status);
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatus(orderStatus)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByTable(Long tableId) {
        log.info("Fetching orders for table id: {}", tableId);
        return orderRepository.findByTableId(tableId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getActiveOrders() {
        log.info("Fetching active orders");
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.CREATED, OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY
        );
        return orderRepository.findByStatusIn(activeStatuses)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching orders between {} and {}", startDate, endDate);
        return orderRepository.findByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByCustomerName(String customerName) {
        log.info("Fetching orders for customer: {}", customerName);
        return orderRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        log.info("Creating new order for table id: {}", orderRequestDTO.getTableId());

        // Find table
        RestaurantTable table = tableRepository.findById(orderRequestDTO.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + orderRequestDTO.getTableId()));

        // Check if table is available
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new RuntimeException("Table is not available. Current status: " + table.getStatus());
        }

        // Create order
        Order order = Order.builder()
                .table(table)
                .customerName(orderRequestDTO.getCustomerName())
                .status(orderRequestDTO.getStatus())
                .totalAmount(BigDecimal.ZERO)
                .build();

        // Add order items if provided
        if (orderRequestDTO.getOrderItems() != null && !orderRequestDTO.getOrderItems().isEmpty()) {
            for (OrderItemRequestDTO itemRequest : orderRequestDTO.getOrderItems()) {
                MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + itemRequest.getMenuItemId()));

                if (!menuItem.getAvailable()) {
                    throw new RuntimeException("Menu item is not available: " + menuItem.getName());
                }

                OrderItem orderItem = OrderItem.builder()
                        .menuItem(menuItem)
                        .quantity(itemRequest.getQuantity())
                        .notes(itemRequest.getNotes())
                        .status(OrderItemStatus.PENDING)
                        .build();

                order.addOrderItem(orderItem);
            }
        }

        // Calculate total amount
        order.setTotalAmount(order.calculateTotalAmount());

        // Update table status
        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());

        return mapToDTO(savedOrder);
    }


    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        log.info("Updating order status for id: {} to {}", id, status);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);

            // If order is paid or cancelled, free the table
            if (newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED) {
                RestaurantTable table = order.getTable();
                table.setStatus(TableStatus.AVAILABLE);
                tableRepository.save(table);
            }

            Order updatedOrder = orderRepository.save(order);
            log.info("Order status updated successfully for id: {}", id);

            return mapToDTO(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }


    @Transactional
    public OrderResponseDTO addItemToOrder(Long orderId, OrderItemRequestDTO orderItemRequestDTO) {
        log.info("Adding item to order id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        MenuItem menuItem = menuItemRepository.findById(orderItemRequestDTO.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + orderItemRequestDTO.getMenuItemId()));

        if (!menuItem.getAvailable()) {
            throw new RuntimeException("Menu item is not available: " + menuItem.getName());
        }

        OrderItem orderItem = OrderItem.builder()
                .menuItem(menuItem)
                .quantity(orderItemRequestDTO.getQuantity())
                .notes(orderItemRequestDTO.getNotes())
                .status(OrderItemStatus.PENDING)
                .build();

        order.addOrderItem(orderItem);
        order.setTotalAmount(order.calculateTotalAmount());

        Order updatedOrder = orderRepository.save(order);
        log.info("Item added successfully to order id: {}", orderId);

        return mapToDTO(updatedOrder);
    }


    @Transactional
    public OrderResponseDTO updateOrderItemQuantity(Long orderId, Long orderItemId, Integer quantity) {
        log.info("Updating quantity for order item id: {} to {}", orderItemId, quantity);

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + orderItemId));

        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to the specified order");
        }

        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);

        // Update order total
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setTotalAmount(order.calculateTotalAmount());
        Order updatedOrder = orderRepository.save(order);

        log.info("Order item quantity updated successfully");
        return mapToDTO(updatedOrder);
    }


    @Transactional
    public OrderResponseDTO removeItemFromOrder(Long orderId, Long orderItemId) {
        log.info("Removing item from order id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + orderItemId));

        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to the specified order");
        }

        order.removeOrderItem(orderItem);
        order.setTotalAmount(order.calculateTotalAmount());

        orderItemRepository.delete(orderItem);
        Order updatedOrder = orderRepository.save(order);

        log.info("Item removed successfully from order id: {}", orderId);
        return mapToDTO(updatedOrder);
    }


    @Transactional
    public OrderResponseDTO cancelOrder(Long id) {
        log.info("Cancelling order with id: {}", id);
        return updateOrderStatus(id, "CANCELLED");
    }


    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Free the table if order is active
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.CANCELLED) {
            RestaurantTable table = order.getTable();
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        orderRepository.delete(order);
        log.info("Order deleted successfully with id: {}", id);
    }


    @Transactional(readOnly = true)
    public OrderStatisticsDTO getOrderStatistics() {
        log.info("Fetching order statistics");

        long totalOrders = orderRepository.count();
        long activeOrders = orderRepository.countByStatus(OrderStatus.CREATED) +
                orderRepository.countByStatus(OrderStatus.CONFIRMED) +
                orderRepository.countByStatus(OrderStatus.PREPARING) +
                orderRepository.countByStatus(OrderStatus.READY);
        long preparingOrders = orderRepository.countByStatus(OrderStatus.PREPARING);
        long readyOrders = orderRepository.countByStatus(OrderStatus.READY);

        // Calculate daily revenue (simplified)
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Order> paidOrdersToday = orderRepository.findPaidOrdersSince(startOfDay);
        BigDecimal dailyRevenue = paidOrdersToday.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderStatisticsDTO(totalOrders, activeOrders, preparingOrders, readyOrders, dailyRevenue);
    }

    private OrderResponseDTO mapToDTO(Order order) {
        // Map table
        RestaurantTableResponseDTO tableDTO = RestaurantTableResponseDTO.builder()
                .id(order.getTable().getId())
                .tableNumber(order.getTable().getTableNumber())
                .capacity(order.getTable().getCapacity())
                .status(order.getTable().getStatus())
                .build();

        // Map order items
        List<OrderItemResponseDTO> orderItemsDTO = order.getOrderItems().stream()
                .map(this::mapOrderItemToDTO)
                .collect(Collectors.toList());

        // Map payments (if needed)
        List<PaymentResponseDTO> paymentsDTO = order.getPayments().stream()
                .map(this::mapPaymentToDTO)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .table(tableDTO)
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(orderItemsDTO)
                .payments(paymentsDTO)
                .build();
    }

    private OrderItemResponseDTO mapOrderItemToDTO(OrderItem orderItem) {
        MenuItemResponseDTO menuItemDTO = MenuItemResponseDTO.builder()
                .id(orderItem.getMenuItem().getId())
                .name(orderItem.getMenuItem().getName())
                .price(orderItem.getMenuItem().getPrice())
                .build();

        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .menuItem(menuItemDTO)
                .quantity(orderItem.getQuantity())
                .notes(orderItem.getNotes())
                .status(orderItem.getStatus())
                .createdAt(orderItem.getCreatedAt())
                .build();
    }

    private PaymentResponseDTO mapPaymentToDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}