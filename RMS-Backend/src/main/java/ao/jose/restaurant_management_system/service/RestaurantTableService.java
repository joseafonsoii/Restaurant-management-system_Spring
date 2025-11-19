package ao.jose.restaurant_management_system.service;

import ao.jose.restaurant_management_system.dto.OrderSummaryDTO;
import ao.jose.restaurant_management_system.dto.request.RestaurantTableRequestDTO;
import ao.jose.restaurant_management_system.dto.response.RestaurantTableResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.TableStatisticsDTO;
import ao.jose.restaurant_management_system.model.Order;
import ao.jose.restaurant_management_system.model.RestaurantTable;
import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import ao.jose.restaurant_management_system.model.enums.TableStatus;
import ao.jose.restaurant_management_system.repository.RestaurantTableRepository;
import ao.jose.restaurant_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final OrderRepository orderRepository;


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> getAllTables() {
        log.info("Fetching all tables");
        return tableRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> getAllTablesOrdered() {
        log.info("Fetching all tables ordered by status and table number");
        return tableRepository.findAllOrderedByStatusAndTableNumber()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public RestaurantTableResponseDTO getTableById(Long id) {
        log.info("Fetching table with id: {}", id);
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
        return mapToDTO(table);
    }


    @Transactional(readOnly = true)
    public RestaurantTableResponseDTO getTableByNumber(String tableNumber) {
        log.info("Fetching table with number: {}", tableNumber);
        RestaurantTable table = tableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Table not found with number: " + tableNumber));
        return mapToDTO(table);
    }


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> getTablesByStatus(String status) {
        log.info("Fetching tables with status: {}", status);
        try {
            TableStatus tableStatus = TableStatus.valueOf(status.toUpperCase());
            return tableRepository.findByStatus(tableStatus)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid table status: " + status);
        }
    }


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> getAvailableTables() {
        log.info("Fetching all available tables");
        return tableRepository.findByStatus(TableStatus.AVAILABLE)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> getAvailableTablesByCapacity(Integer requiredCapacity) {
        log.info("Fetching available tables with capacity >= {}", requiredCapacity);

        if (requiredCapacity == null || requiredCapacity <= 0) {
            throw new RuntimeException("Required capacity must be greater than 0");
        }

        return tableRepository.findAvailableTablesByCapacity(requiredCapacity)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> getTablesByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        log.info("Fetching tables with capacity between {} and {}", minCapacity, maxCapacity);

        if (minCapacity == null) minCapacity = 1;
        if (maxCapacity == null) maxCapacity = 100; // Valor máximo razoável

        if (minCapacity <= 0 || maxCapacity < minCapacity) {
            throw new RuntimeException("Invalid capacity range");
        }

        return tableRepository.findByCapacityBetween(minCapacity, maxCapacity)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDTO> searchTablesByNumber(String tableNumber) {
        log.info("Searching tables with number containing: {}", tableNumber);
        return tableRepository.findByTableNumberContainingIgnoreCase(tableNumber)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public RestaurantTableResponseDTO createTable(RestaurantTableRequestDTO tableRequestDTO) {
        log.info("Creating new table: {}", tableRequestDTO.getTableNumber());

        // Check if table number already exists
        if (tableRepository.existsByTableNumber(tableRequestDTO.getTableNumber())) {
            throw new RuntimeException("Table with number '" + tableRequestDTO.getTableNumber() + "' already exists");
        }

        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(tableRequestDTO.getTableNumber())
                .capacity(tableRequestDTO.getCapacity())
                .status(tableRequestDTO.getStatus())
                .build();

        RestaurantTable savedTable = tableRepository.save(table);
        log.info("Table created successfully with id: {}", savedTable.getId());

        return mapToDTO(savedTable);
    }


    @Transactional
    public RestaurantTableResponseDTO updateTable(Long id, RestaurantTableRequestDTO tableRequestDTO) {
        log.info("Updating table with id: {}", id);

        RestaurantTable existingTable = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        // Check if table number already exists for another table
        if (tableRepository.existsByTableNumberAndIdNot(tableRequestDTO.getTableNumber(), id)) {
            throw new RuntimeException("Table with number '" + tableRequestDTO.getTableNumber() + "' already exists");
        }

        existingTable.setTableNumber(tableRequestDTO.getTableNumber());
        existingTable.setCapacity(tableRequestDTO.getCapacity());
        existingTable.setStatus(tableRequestDTO.getStatus());

        RestaurantTable updatedTable = tableRepository.save(existingTable);
        log.info("Table updated successfully with id: {}", updatedTable.getId());

        return mapToDTO(updatedTable);
    }


    @Transactional
    public RestaurantTableResponseDTO updateTableStatus(Long id, String status) {
        log.info("Updating table status for id: {} to {}", id, status);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        try {
            TableStatus newStatus = TableStatus.valueOf(status.toUpperCase());
            table.setStatus(newStatus);

            RestaurantTable updatedTable = tableRepository.save(table);
            log.info("Table status updated successfully for id: {}", id);

            return mapToDTO(updatedTable);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid table status: " + status);
        }
    }


    @Transactional
    public RestaurantTableResponseDTO occupyTable(Long id) {
        log.info("Occupying table with id: {}", id);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        if (!table.isAvailable()) {
            throw new RuntimeException("Table is not available. Current status: " + table.getStatus());
        }

        table.occupy();
        RestaurantTable occupiedTable = tableRepository.save(table);

        log.info("Table occupied successfully with id: {}", id);
        return mapToDTO(occupiedTable);
    }


    @Transactional
    public RestaurantTableResponseDTO freeTable(Long id) {
        log.info("Freeing table with id: {}", id);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        table.free();
        RestaurantTable freedTable = tableRepository.save(table);

        log.info("Table freed successfully with id: {}", id);
        return mapToDTO(freedTable);
    }


    @Transactional
    public RestaurantTableResponseDTO reserveTable(Long id) {
        log.info("Reserving table with id: {}", id);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        if (!table.isAvailable()) {
            throw new RuntimeException("Table is not available for reservation. Current status: " + table.getStatus());
        }

        table.reserve();
        RestaurantTable reservedTable = tableRepository.save(table);

        log.info("Table reserved successfully with id: {}", id);
        return mapToDTO(reservedTable);
    }


    @Transactional
    public void deleteTable(Long id) {
        log.info("Deleting table with id: {}", id);

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        // Check if table has active orders
        if (table.getActiveOrder() != null) {
            throw new RuntimeException("Cannot delete table with active orders");
        }

        tableRepository.delete(table);
        log.info("Table deleted successfully with id: {}", id);
    }


    @Transactional(readOnly = true)
    public TableStatisticsDTO getTableStatistics() {
        log.info("Fetching table statistics");

        Long totalTables = tableRepository.count();
        Long availableTables = tableRepository.countAvailableTables() != null ?
                tableRepository.countAvailableTables() : 0L;
        Long occupiedTables = tableRepository.countOccupiedTables() != null ?
                tableRepository.countOccupiedTables() : 0L;
        Long reservedTables = tableRepository.countReservedTables() != null ?
                tableRepository.countReservedTables() : 0L;

        // Calculate occupancy rate
        double occupancyRate = totalTables > 0 ?
                (double) (occupiedTables + reservedTables) / totalTables * 100 : 0.0;

        // Get tables by status
        Map<TableStatus, Long> tablesByStatus = tableRepository.countTablesByStatus()
                .stream()
                .collect(Collectors.toMap(
                        obj -> (TableStatus) obj[0],
                        obj -> (Long) obj[1]
                ));

        return TableStatisticsDTO.builder()
                .totalTables(totalTables)
                .availableTables(availableTables)
                .occupiedTables(occupiedTables)
                .reservedTables(reservedTables)
                .tablesByStatus(tablesByStatus)
                .occupancyRate(occupancyRate)
                .build();
    }


    @Transactional(readOnly = true)
    public boolean tableExistsByNumber(String tableNumber) {
        return tableRepository.existsByTableNumber(tableNumber);
    }


    @Transactional(readOnly = true)
    public boolean isTableAvailable(Long id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
        return table.isAvailable();
    }

    private RestaurantTableResponseDTO mapToDTO(RestaurantTable table) {
        // Get active order info
        Order activeOrder = table.getActiveOrder();
        OrderSummaryDTO activeOrderSummary = null;
        int activeOrdersCount = 0;

        if (activeOrder != null) {
            activeOrderSummary = OrderSummaryDTO.builder()
                    .orderId(activeOrder.getId())
                    .customerName(activeOrder.getCustomerName())
                    .tableNumber(table.getTableNumber())
                    .orderTotal(activeOrder.getTotalAmount())
                    .orderStatus(activeOrder.getStatus())
                    .build();

            // Count active orders (should typically be 1, but just in case)
            activeOrdersCount = (int) table.getOrders().stream()
                    .filter(order -> order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.CANCELLED)
                    .count();
        }

        return RestaurantTableResponseDTO.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .activeOrder(activeOrderSummary)
                .activeOrdersCount(activeOrdersCount)
                .build();
    }
}