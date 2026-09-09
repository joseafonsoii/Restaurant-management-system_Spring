package ao.jose.restaurant_management_system.dto.response;

import ao.jose.restaurant_management_system.dto.OrderSummaryDTO;
import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import ao.jose.restaurant_management_system.model.enums.PaymentStatus;
import ao.jose.restaurant_management_system.model.enums.PaymentMethod;
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
public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private OrderSummaryDTO orderSummary;
}

