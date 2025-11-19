package ao.jose.restaurant_management_system.repository;

import ao.jose.restaurant_management_system.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategoryId(Long categoryId);

    List<MenuItem> findByAvailable(Boolean available);

    List<MenuItem> findByCategoryIdAndAvailable(Long categoryId, Boolean available);

    List<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.category.id = :categoryId ORDER BY mi.name ASC")
    List<MenuItem> findByCategoryIdOrdered(@Param("categoryId") Long categoryId);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.available = true ORDER BY mi.category.displayOrder ASC, mi.name ASC")
    List<MenuItem> findAllAvailableOrdered();

    @Query("SELECT mi FROM MenuItem mi WHERE LOWER(mi.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<MenuItem> findByNameContainingIgnoreCase(@Param("name") String name);

    boolean existsByNameAndCategoryId(String name, Long categoryId);

    boolean existsByNameAndCategoryIdAndIdNot(String name, Long categoryId, Long id);

    @Query("SELECT COUNT(mi) FROM MenuItem mi WHERE mi.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT * FROM menu_item WHERE JSON_CONTAINS(tags, :tag, '$')", nativeQuery = true)
    List<MenuItem> findByTag(@Param("tag") String tag);
}