package ao.jose.restaurant_management_system.model;

import ao.jose.restaurant_management_system.model.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Método para calcular o subtotal do item
    public Double getSubtotal() {
        if (menuItem != null && menuItem.getPrice() != null && quantity != null) {
            return menuItem.getPrice().doubleValue() * quantity;
        }
        return 0.0;
    }

    // Método para marcar como concluído
    public void markAsCompleted() {
        this.status = OrderItemStatus.COMPLETED;
    }

    // Método para marcar como pendente
    public void markAsPending() {
        this.status = OrderItemStatus.PENDING;
    }

    // Método para marcar como falha
    public void markAsFailed() {
        this.status = OrderItemStatus.FAILED;
    }

    // Método para marcar como reembolsado
    public void markAsRefunded() {
        this.status = OrderItemStatus.REFUNDED;
    }

    // Método para verificar se está pendente
    public boolean isPending() {
        return this.status == OrderItemStatus.PENDING;
    }

    // Método para verificar se está concluído
    public boolean isCompleted() {
        return this.status == OrderItemStatus.COMPLETED;
    }
}

