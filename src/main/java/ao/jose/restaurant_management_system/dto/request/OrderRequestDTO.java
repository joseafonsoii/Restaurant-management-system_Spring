package ao.jose.restaurant_management_system.dto.request;

import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull(message = "Table ID is required")
    private Long tableId;

    private String customerName;

    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    private List<OrderItemRequestDTO> orderItems;
}

