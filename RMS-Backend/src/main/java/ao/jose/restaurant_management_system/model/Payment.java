package ao.jose.restaurant_management_system.model;

import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import ao.jose.restaurant_management_system.model.enums.PaymentMethod;
import ao.jose.restaurant_management_system.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Método para processar pagamento
    public void processPayment() {
        if (this.status == PaymentStatus.PENDING) {
            this.status = PaymentStatus.COMPLETED;

            // Atualizar status do pedido para PAID
            if (this.order != null) {
                this.order.setStatus(OrderStatus.PAID);
            }
        }
    }

    // Método para reembolsar pagamento
    public void refundPayment() {
        if (this.status == PaymentStatus.COMPLETED) {
            this.status = PaymentStatus.REFUNDED;
        }
    }
}

