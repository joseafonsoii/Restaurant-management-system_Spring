package ao.jose.restaurant_management_system.dto.request;

import ao.jose.restaurant_management_system.model.enums.PaymentMethod;
import ao.jose.restaurant_management_system.model.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
}