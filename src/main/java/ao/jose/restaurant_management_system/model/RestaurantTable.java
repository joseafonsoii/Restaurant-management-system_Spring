package ao.jose.restaurant_management_system.model;

import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import ao.jose.restaurant_management_system.model.enums.TableStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true, length = 10)
    private String tableNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    // Método para verificar se a mesa está disponível
    public boolean isAvailable() {
        return this.status == TableStatus.AVAILABLE;
    }

    // Método para ocupar a mesa
    public void occupy() {
        if (this.status == TableStatus.AVAILABLE) {
            this.status = TableStatus.OCCUPIED;
        } else {
            throw new IllegalStateException("Table is not available. Current status: " + this.status);
        }
    }

    // Método para liberar a mesa
    public void free() {
        this.status = TableStatus.AVAILABLE;
    }

    // Método para reservar a mesa
    public void reserve() {
        if (this.status == TableStatus.AVAILABLE) {
            this.status = TableStatus.RESERVED;
        } else {
            throw new IllegalStateException("Table is not available for reservation. Current status: " + this.status);
        }
    }

    // Método para obter o pedido ativo (não pago/não cancelado)
    public Order getActiveOrder() {
        return this.orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.CANCELLED)
                .findFirst()
                .orElse(null);
    }
}
