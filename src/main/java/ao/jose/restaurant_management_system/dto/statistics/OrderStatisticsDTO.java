package ao.jose.restaurant_management_system.dto.statistics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatisticsDTO {
    private long totalOrders;
    private long activeOrders;
    private long preparingOrders;
    private long readyOrders;
    private BigDecimal dailyRevenue;



}