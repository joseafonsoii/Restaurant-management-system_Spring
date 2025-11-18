package ao.jose.restaurant_management_system.dto.response;

import ao.jose.restaurant_management_system.dto.OrderSummaryDTO;
import ao.jose.restaurant_management_system.model.enums.TableStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTableResponseDTO {

    private Long id;
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderSummaryDTO activeOrder; // Pedido ativo na mesa
    private Integer activeOrdersCount; // Número de pedidos ativos

    public RestaurantTableResponseDTO(Long id, String tableNumber, Integer capacity, TableStatus status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }
}