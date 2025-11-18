package ao.jose.restaurant_management_system.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemStatisticsDTO {
    private Long totalOrderItems;
    private Long pendingOrderItems;
    private Long completedOrderItems;
    private Long todayCompletedItems;
    private Double averageItemsPerOrder;


}
