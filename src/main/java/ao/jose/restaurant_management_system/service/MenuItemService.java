package ao.jose.restaurant_management_system.service;

import ao.jose.restaurant_management_system.dto.request.MenuItemRequestDTO;
import ao.jose.restaurant_management_system.dto.response.MenuItemResponseDTO;
import ao.jose.restaurant_management_system.dto.response.CategoryResponseDTO;
import ao.jose.restaurant_management_system.model.MenuItem;
import ao.jose.restaurant_management_system.model.Category;
import ao.jose.restaurant_management_system.repository.MenuItemRepository;
import ao.jose.restaurant_management_system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getAllMenuItems() {
        log.info("Fetching all menu items");
        return menuItemRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getAvailableMenuItems() {
        log.info("Fetching all available menu items");
        return menuItemRepository.findAllAvailableOrdered()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public MenuItemResponseDTO getMenuItemById(Long id) {
        log.info("Fetching menu item with id: {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        return mapToDTO(menuItem);
    }


    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getMenuItemsByCategory(Long categoryId) {
        log.info("Fetching menu items for category id: {}", categoryId);
        return menuItemRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getAvailableMenuItemsByCategory(Long categoryId) {
        log.info("Fetching available menu items for category id: {}", categoryId);
        return menuItemRepository.findByCategoryIdAndAvailable(categoryId, true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching menu items with price between {} and {}", minPrice, maxPrice);

        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = new BigDecimal("999999.99");

        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(minPrice) < 0) {
            throw new RuntimeException("Invalid price range");
        }

        return menuItemRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> searchMenuItemsByName(String name) {
        log.info("Searching menu items with name containing: {}", name);
        return menuItemRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO menuItemRequestDTO) {
        log.info("Creating new menu item: {}", menuItemRequestDTO.getName());

        // Check if menu item name already exists in the same category
        if (menuItemRepository.existsByNameAndCategoryId(
                menuItemRequestDTO.getName(), menuItemRequestDTO.getCategoryId())) {
            throw new RuntimeException("Menu item with name '" + menuItemRequestDTO.getName() +
                    "' already exists in this category");
        }

        // Find category
        Category category = categoryRepository.findById(menuItemRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + menuItemRequestDTO.getCategoryId()));

        MenuItem menuItem = MenuItem.builder()
                .name(menuItemRequestDTO.getName())
                .description(menuItemRequestDTO.getDescription())
                .price(menuItemRequestDTO.getPrice())
                .category(category)
                .preparationTime(menuItemRequestDTO.getPreparationTime())
                .available(menuItemRequestDTO.getAvailable() != null ? menuItemRequestDTO.getAvailable() : true)
                .ingredients(menuItemRequestDTO.getIngredients())
                .tags(menuItemRequestDTO.getTags())
                .build();

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully with id: {}", savedMenuItem.getId());

        return mapToDTO(savedMenuItem);
    }


    @Transactional
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO menuItemRequestDTO) {
        log.info("Updating menu item with id: {}", id);

        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));

        // Check if menu item name already exists in the same category for another item
        if (menuItemRepository.existsByNameAndCategoryIdAndIdNot(
                menuItemRequestDTO.getName(), menuItemRequestDTO.getCategoryId(), id)) {
            throw new RuntimeException("Menu item with name '" + menuItemRequestDTO.getName() +
                    "' already exists in this category");
        }

        // Find category if changed
        Category category = existingMenuItem.getCategory();
        if (!existingMenuItem.getCategory().getId().equals(menuItemRequestDTO.getCategoryId())) {
            category = categoryRepository.findById(menuItemRequestDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + menuItemRequestDTO.getCategoryId()));
        }

        existingMenuItem.setName(menuItemRequestDTO.getName());
        existingMenuItem.setDescription(menuItemRequestDTO.getDescription());
        existingMenuItem.setPrice(menuItemRequestDTO.getPrice());
        existingMenuItem.setCategory(category);
        existingMenuItem.setPreparationTime(menuItemRequestDTO.getPreparationTime());
        existingMenuItem.setAvailable(menuItemRequestDTO.getAvailable());
        existingMenuItem.setIngredients(menuItemRequestDTO.getIngredients());
        existingMenuItem.setTags(menuItemRequestDTO.getTags());

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        log.info("Menu item updated successfully with id: {}", updatedMenuItem.getId());

        return mapToDTO(updatedMenuItem);
    }


    @Transactional
    public void deleteMenuItem(Long id) {
        log.info("Deleting menu item with id: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));

        // You might want to check if menu item has order items before deleting

        menuItemRepository.delete(menuItem);
        log.info("Menu item deleted successfully with id: {}", id);
    }


    @Transactional
    public MenuItemResponseDTO updateAvailability(Long id, Boolean available) {
        log.info("Updating availability for menu item id: {} to {}", id, available);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));

        menuItem.setAvailable(available);
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        log.info("Menu item availability updated successfully for id: {}", id);
        return mapToDTO(updatedMenuItem);
    }


    @Transactional(readOnly = true)
    public long countMenuItemsByCategory(Long categoryId) {
        return menuItemRepository.countByCategoryId(categoryId);
    }

    private MenuItemResponseDTO mapToDTO(MenuItem menuItem) {
        // Map category to DTO
        CategoryResponseDTO categoryDTO = CategoryResponseDTO.builder()
                .id(menuItem.getCategory().getId())
                .name(menuItem.getCategory().getName())
                .description(menuItem.getCategory().getDescription())
                .displayOrder(menuItem.getCategory().getDisplayOrder())
                .createdAt(menuItem.getCategory().getCreatedAt())
                .updatedAt(menuItem.getCategory().getUpdatedAt())
                .build();

        return MenuItemResponseDTO.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(categoryDTO)
                .preparationTime(menuItem.getPreparationTime())
                .available(menuItem.getAvailable())
                .ingredients(menuItem.getIngredients())
                .tags(menuItem.getTags())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }
}