package ao.jose.restaurant_management_system.dto.statistics;

import ao.jose.restaurant_management_system.model.enums.TableStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableStatisticsDTO {

    private Long totalTables;
    private Long availableTables;
    private Long occupiedTables;
    private Long reservedTables;
    private Map<TableStatus, Long> tablesByStatus;
    private Double occupancyRate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TableOccupancyDTO {
        private String tableNumber;
        private Integer capacity;
        private TableStatus status;
        private Long orderCount;
        private String customerName;
    }
}