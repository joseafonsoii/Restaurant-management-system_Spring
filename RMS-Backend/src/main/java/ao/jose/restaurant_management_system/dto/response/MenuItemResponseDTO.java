package ao.jose.restaurant_management_system.dto.response;

import ao.jose.restaurant_management_system.dto.response.CategoryResponseDTO;
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
public class MenuItemResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private CategoryResponseDTO category;
    private Integer preparationTime;
    private Boolean available;
    private String ingredients;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}