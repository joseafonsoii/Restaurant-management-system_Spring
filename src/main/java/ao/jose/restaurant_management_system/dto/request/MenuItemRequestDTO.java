package ao.jose.restaurant_management_system.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequestDTO {

    @NotBlank(message = "Menu item name is required")
    @Size(max = 100, message = "Menu item name must not exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Integer preparationTime;

    @Builder.Default
    private Boolean available = true;

    private String ingredients; // JSON string

    private String tags; // JSON string
}