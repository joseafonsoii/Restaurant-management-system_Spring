package ao.jose.restaurant_management_system.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "preparation_time")
    private Integer preparationTime;

    @Column(name = "available")
    private Boolean available;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ingredients", columnDefinition = "json")
    private String ingredients;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "json")
    private String tags;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtor para facilitar a criação
    public MenuItem(String name, String description, BigDecimal price, Category category,
                    Integer preparationTime, Boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.preparationTime = preparationTime;
        this.available = available;
    }
}