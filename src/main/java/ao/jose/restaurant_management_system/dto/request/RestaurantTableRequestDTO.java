package ao.jose.restaurant_management_system.dto.request;

import ao.jose.restaurant_management_system.model.enums.TableStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTableRequestDTO {

    @NotBlank(message = "Table number is required")
    private String tableNumber;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than 0")
    private Integer capacity;

    @Builder.Default
    private TableStatus status = TableStatus.AVAILABLE;
}