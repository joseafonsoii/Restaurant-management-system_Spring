package ao.jose.restaurant_management_system.dto.statistics;

import ao.jose.restaurant_management_system.model.enums.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatisticsDTO {

    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal monthlyRevenue;
    private Long totalTransactions;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Map<PaymentMethod, BigDecimal> revenueByMethod;
    private Map<PaymentMethod, Long> transactionsByMethod;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyRevenueDTO {
        private String date;
        private BigDecimal revenue;
        private Long transactionCount;
    }
}