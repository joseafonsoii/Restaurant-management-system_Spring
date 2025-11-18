package ao.jose.restaurant_management_system.dto;

import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryDTO {
    private Long orderId;
    private String customerName;
    private String tableNumber;
    private BigDecimal orderTotal;
    private OrderStatus orderStatus;
}