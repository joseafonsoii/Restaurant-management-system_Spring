package ao.jose.restaurant_management_system.dto.response;

import ao.jose.restaurant_management_system.dto.response.MenuItemResponseDTO;
import ao.jose.restaurant_management_system.model.enums.OrderItemStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {

    private Long id;
    private Long orderId;
    private MenuItemResponseDTO menuItem;
    private Integer quantity;
    private String notes;
    private OrderItemStatus status;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
}

