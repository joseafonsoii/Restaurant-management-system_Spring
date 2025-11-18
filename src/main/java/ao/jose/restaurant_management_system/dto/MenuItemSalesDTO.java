package ao.jose.restaurant_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemSalesDTO {
    private Long menuItemId;
    private String menuItemName;
    private String categoryName;
    private Long totalSold;
    private BigDecimal totalRevenue;


}
