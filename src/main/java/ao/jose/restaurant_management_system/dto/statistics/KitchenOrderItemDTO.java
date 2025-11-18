package ao.jose.restaurant_management_system.dto.statistics;

import ao.jose.restaurant_management_system.model.enums.OrderItemStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenOrderItemDTO {

    private Long id;
    private Long orderId;
    private String tableNumber;
    private String customerName;
    private String menuItemName;
    private Integer quantity;
    private String notes;
    private OrderItemStatus status;
    private Integer preparationTime; // em minutos
    private LocalDateTime createdAt;
    private LocalDateTime orderCreatedAt;

    // Para agrupamento na cozinha
    private String categoryName;
    private String specialInstructions;
}