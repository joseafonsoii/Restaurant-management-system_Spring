package ao.jose.restaurant_management_system.repository;

import ao.jose.restaurant_management_system.model.Payment;
import ao.jose.restaurant_management_system.model.enums.PaymentMethod;
import ao.jose.restaurant_management_system.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByMethod(PaymentMethod method);

    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.status = 'COMPLETED'")
    List<Payment> findCompletedPaymentsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt >= :startDate")
    BigDecimal getRevenueSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED'")
    Long countSuccessfulTransactions();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED'")
    Long countFailedTransactions();

    @Query("SELECT p.method, SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' GROUP BY p.method")
    List<Object[]> getRevenueByPaymentMethod();

    @Query("SELECT p.method, COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' GROUP BY p.method")
    List<Object[]> getTransactionCountByPaymentMethod();

    @Query("SELECT DATE(p.createdAt), SUM(p.amount), COUNT(p) FROM Payment p " +
            "WHERE p.status = 'COMPLETED' AND p.createdAt >= :startDate " +
            "GROUP BY DATE(p.createdAt) " +
            "ORDER BY DATE(p.createdAt) DESC")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Payment p WHERE p.order.table.tableNumber = :tableNumber AND p.status = 'COMPLETED'")
    List<Payment> findCompletedPaymentsByTableNumber(@Param("tableNumber") String tableNumber);

    @Query("SELECT p FROM Payment p WHERE p.order.customerName LIKE %:customerName% AND p.status = 'COMPLETED'")
    List<Payment> findCompletedPaymentsByCustomerName(@Param("customerName") String customerName);
}