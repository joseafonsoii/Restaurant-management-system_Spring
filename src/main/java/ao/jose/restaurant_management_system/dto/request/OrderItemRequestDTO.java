package ao.jose.restaurant_management_system.dto.request;

import ao.jose.restaurant_management_system.model.enums.OrderItemStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    private String notes;

    @Builder.Default
    private OrderItemStatus status = OrderItemStatus.PENDING;
}