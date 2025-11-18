package ao.jose.restaurant_management_system.repository;

import ao.jose.restaurant_management_system.model.RestaurantTable;
import ao.jose.restaurant_management_system.model.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByTableNumber(String tableNumber);

    List<RestaurantTable> findByStatus(TableStatus status);

    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);

    List<RestaurantTable> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);

    boolean existsByTableNumber(String tableNumber);

    boolean existsByTableNumberAndIdNot(String tableNumber, Long id);

    @Query("SELECT t FROM RestaurantTable t WHERE t.status = 'AVAILABLE' AND t.capacity >= :requiredCapacity ORDER BY t.capacity ASC")
    List<RestaurantTable> findAvailableTablesByCapacity(@Param("requiredCapacity") Integer requiredCapacity);

    @Query("SELECT t FROM RestaurantTable t WHERE t.status IN ('OCCUPIED', 'RESERVED') ORDER BY t.tableNumber ASC")
    List<RestaurantTable> findOccupiedAndReservedTables();

    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.status = 'AVAILABLE'")
    Long countAvailableTables();

    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.status = 'OCCUPIED'")
    Long countOccupiedTables();

    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.status = 'RESERVED'")
    Long countReservedTables();

    @Query("SELECT t.status, COUNT(t) FROM RestaurantTable t GROUP BY t.status")
    List<Object[]> countTablesByStatus();

    @Query("SELECT t FROM RestaurantTable t ORDER BY " +
            "CASE WHEN t.status = 'OCCUPIED' THEN 1 " +
            "WHEN t.status = 'RESERVED' THEN 2 " +
            "ELSE 3 END, t.tableNumber ASC")
    List<RestaurantTable> findAllOrderedByStatusAndTableNumber();

    @Query("SELECT t FROM RestaurantTable t WHERE LOWER(t.tableNumber) LIKE LOWER(CONCAT('%', :tableNumber, '%'))")
    List<RestaurantTable> findByTableNumberContainingIgnoreCase(@Param("tableNumber") String tableNumber);
}