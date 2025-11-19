package ao.jose.restaurant_management_system.repository;

import ao.jose.restaurant_management_system.model.Order;
import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTableId(Long tableId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);

    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.status NOT IN ('CANCELLED', 'PAID')")
    List<Order> findActiveOrdersByTableId(@Param("tableId") Long tableId);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findByStatusIn(@Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.status = 'PAID'")
    List<Order> findPaidOrdersSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT o FROM Order o WHERE o.table.tableNumber = :tableNumber AND o.status NOT IN ('CANCELLED', 'PAID')")
    Optional<Order> findActiveOrderByTableNumber(@Param("tableNumber") String tableNumber);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.menuItem.id = :menuItemId")
    List<Order> findByMenuItemId(@Param("menuItemId") Long menuItemId);
}