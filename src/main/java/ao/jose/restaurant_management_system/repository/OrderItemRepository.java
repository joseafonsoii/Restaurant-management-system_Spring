package ao.jose.restaurant_management_system.repository;

import ao.jose.restaurant_management_system.model.OrderItem;
import ao.jose.restaurant_management_system.model.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByMenuItemId(Long menuItemId);

    List<OrderItem> findByStatus(OrderItemStatus status);

    List<OrderItem> findByOrderIdAndStatus(Long orderId, OrderItemStatus status);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId ORDER BY oi.createdAt ASC")
    List<OrderItem> findByOrderIdOrderByCreatedAt(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.status IN ('PENDING', 'COMPLETED') AND oi.order.status NOT IN ('CANCELLED', 'PAID')")
    List<OrderItem> findActiveOrderItems();

    @Query("SELECT oi FROM OrderItem oi WHERE oi.status = 'PENDING' AND oi.order.status IN ('CONFIRMED', 'PREPARING')")
    List<OrderItem> findPendingKitchenItems();

    @Query("SELECT oi FROM OrderItem oi WHERE oi.status = 'PENDING' AND oi.menuItem.category.id = :categoryId")
    List<OrderItem> findPendingItemsByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItem> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT oi.menuItem.category.name, COUNT(oi) FROM OrderItem oi " +
            "WHERE oi.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.menuItem.category.name")
    List<Object[]> countOrderItemsByCategory(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.table.tableNumber = :tableNumber AND oi.status = 'PENDING'")
    List<OrderItem> findPendingItemsByTableNumber(@Param("tableNumber") String tableNumber);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.status = 'PENDING'")
    Long countPendingOrderItems();

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.status = 'COMPLETED' AND oi.createdAt >= :startDate")
    Long countCompletedItemsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT oi.menuItem, SUM(oi.quantity) FROM OrderItem oi " +
            "WHERE oi.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.menuItem " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findPopularMenuItems(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.menuItem.id = :menuItemId")
    List<OrderItem> findByOrderIdAndMenuItemId(@Param("orderId") Long orderId,
                                               @Param("menuItemId") Long menuItemId);

    void deleteByOrderId(Long orderId);
}