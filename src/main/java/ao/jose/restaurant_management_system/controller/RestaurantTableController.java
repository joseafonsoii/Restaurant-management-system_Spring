package ao.jose.restaurant_management_system.controller;

import ao.jose.restaurant_management_system.dto.request.RestaurantTableRequestDTO;
import ao.jose.restaurant_management_system.dto.response.RestaurantTableResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.TableStatisticsDTO;
import ao.jose.restaurant_management_system.service.RestaurantTableService;
//import jakarta.validation.Valid;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @GetMapping
    public ResponseEntity<List<RestaurantTableResponseDTO>> getAllTables(
            @RequestParam(required = false) Boolean ordered,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity) {

        List<RestaurantTableResponseDTO> tables;

        if (Boolean.TRUE.equals(ordered)) {
            tables = tableService.getAllTablesOrdered();
        } else if (status != null) {
            tables = tableService.getTablesByStatus(status);
        } else if (capacity != null) {
            tables = tableService.getAvailableTablesByCapacity(capacity);
        } else if (minCapacity != null || maxCapacity != null) {
            tables = tableService.getTablesByCapacityRange(minCapacity, maxCapacity);
        } else {
            tables = tableService.getAllTables();
        }

        return ResponseEntity.ok(tables);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RestaurantTableResponseDTO>> getAvailableTables(
            @RequestParam(required = false) Integer requiredCapacity) {

        List<RestaurantTableResponseDTO> tables = (requiredCapacity != null) ?
                tableService.getAvailableTablesByCapacity(requiredCapacity) :
                tableService.getAvailableTables();

        return ResponseEntity.ok(tables);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantTableResponseDTO>> searchTablesByNumber(
            @RequestParam String tableNumber) {

        List<RestaurantTableResponseDTO> tables = tableService.searchTablesByNumber(tableNumber);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableResponseDTO> getTableById(@PathVariable Long id) {
        RestaurantTableResponseDTO table = tableService.getTableById(id);
        return ResponseEntity.ok(table);
    }

    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<RestaurantTableResponseDTO> getTableByNumber(@PathVariable String tableNumber) {
        RestaurantTableResponseDTO table = tableService.getTableByNumber(tableNumber);
        return ResponseEntity.ok(table);
    }

    @PostMapping
    public ResponseEntity<RestaurantTableResponseDTO> createTable( @RequestBody RestaurantTableRequestDTO tableRequestDTO) {
        RestaurantTableResponseDTO createdTable = tableService.createTable(tableRequestDTO);
        return new ResponseEntity<>(createdTable, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTableResponseDTO> updateTable(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantTableRequestDTO tableRequestDTO) {

        RestaurantTableResponseDTO updatedTable = tableService.updateTable(id, tableRequestDTO);
        return ResponseEntity.ok(updatedTable);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantTableResponseDTO> updateTableStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        RestaurantTableResponseDTO updatedTable = tableService.updateTableStatus(id, status);
        return ResponseEntity.ok(updatedTable);
    }

    @PostMapping("/{id}/occupy")
    public ResponseEntity<RestaurantTableResponseDTO> occupyTable(@PathVariable Long id) {
        RestaurantTableResponseDTO occupiedTable = tableService.occupyTable(id);
        return ResponseEntity.ok(occupiedTable);
    }

    @PostMapping("/{id}/free")
    public ResponseEntity<RestaurantTableResponseDTO> freeTable(@PathVariable Long id) {
        RestaurantTableResponseDTO freedTable = tableService.freeTable(id);
        return ResponseEntity.ok(freedTable);
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<RestaurantTableResponseDTO> reserveTable(@PathVariable Long id) {
        RestaurantTableResponseDTO reservedTable = tableService.reserveTable(id);
        return ResponseEntity.ok(reservedTable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<TableStatisticsDTO> getTableStatistics() {
        TableStatisticsDTO statistics = tableService.getTableStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/exists/{tableNumber}")
    public ResponseEntity<Boolean> checkTableExists(@PathVariable String tableNumber) {
        boolean exists = tableService.tableExistsByNumber(tableNumber);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> checkTableAvailable(@PathVariable Long id) {
        boolean available = tableService.isTableAvailable(id);
        return ResponseEntity.ok(available);
    }
}