package ao.jose.restaurant_management_system.dto.request;

//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {

    //@NotBlank(message = "Category name is required")
    //@Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;

    private String description;

    private Integer displayOrder;
}