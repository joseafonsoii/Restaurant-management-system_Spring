package ao.jose.restaurant_management_system.controller;

import ao.jose.restaurant_management_system.dto.request.MenuItemRequestDTO;
import ao.jose.restaurant_management_system.dto.response.MenuItemResponseDTO;
import ao.jose.restaurant_management_system.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<List<MenuItemResponseDTO>> getAllMenuItems(
            @RequestParam(required = false) Boolean available) {

        List<MenuItemResponseDTO> menuItems = (available != null && available) ?
                menuItemService.getAvailableMenuItems() :
                menuItemService.getAllMenuItems();

        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemById(@PathVariable Long id) {
        MenuItemResponseDTO menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Boolean available) {

        List<MenuItemResponseDTO> menuItems = (available != null && available) ?
                menuItemService.getAvailableMenuItemsByCategory(categoryId) :
                menuItemService.getMenuItemsByCategory(categoryId);

        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemResponseDTO>> searchMenuItemsByName(
            @RequestParam String name) {

        List<MenuItemResponseDTO> menuItems = menuItemService.searchMenuItemsByName(name);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByPriceRange(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        List<MenuItemResponseDTO> menuItems = menuItemService.getMenuItemsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(menuItems);
    }

    @PostMapping
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(
            @Valid @RequestBody MenuItemRequestDTO menuItemRequestDTO) {

        MenuItemResponseDTO createdMenuItem = menuItemService.createMenuItem(menuItemRequestDTO);
        return new ResponseEntity<>(createdMenuItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequestDTO menuItemRequestDTO) {

        MenuItemResponseDTO updatedMenuItem = menuItemService.updateMenuItem(id, menuItemRequestDTO);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<MenuItemResponseDTO> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available) {

        MenuItemResponseDTO updatedMenuItem = menuItemService.updateAvailability(id, available);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}/count")
    public ResponseEntity<Long> countMenuItemsByCategory(@PathVariable Long categoryId) {
        long count = menuItemService.countMenuItemsByCategory(categoryId);
        return ResponseEntity.ok(count);
    }
}