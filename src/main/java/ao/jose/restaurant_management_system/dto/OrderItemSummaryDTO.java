package ao.jose.restaurant_management_system.dto;

import ao.jose.restaurant_management_system.model.enums.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemSummaryDTO {
    private Long id;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private OrderItemStatus status;
    private String notes;
}
