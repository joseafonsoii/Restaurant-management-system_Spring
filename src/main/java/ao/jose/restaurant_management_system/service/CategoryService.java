package ao.jose.restaurant_management_system.service;

import ao.jose.restaurant_management_system.dto.request.CategoryRequestDTO;
import ao.jose.restaurant_management_system.dto.response.CategoryResponseDTO;
import ao.jose.restaurant_management_system.model.Category;
import ao.jose.restaurant_management_system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService{

    private final CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategoriesOrdered() {
        log.info("Fetching all categories ordered by display order");
        return categoryRepository.findAllOrderedByDisplayOrderAndName()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return mapToDTO(category);
    }


    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        log.info("Creating new category: {}", categoryRequestDTO.getName());

        // Check if category name already exists
        if (categoryRepository.existsByName(categoryRequestDTO.getName())) {
            throw new RuntimeException("Category with name '" + categoryRequestDTO.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(categoryRequestDTO.getName())
                .description(categoryRequestDTO.getDescription())
                .displayOrder(categoryRequestDTO.getDisplayOrder() != null ?
                        categoryRequestDTO.getDisplayOrder() : 0)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());

        return mapToDTO(savedCategory);
    }


    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        log.info("Updating category with id: {}", id);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if category name already exists for another category
        if (categoryRepository.existsByNameAndIdNot(categoryRequestDTO.getName(), id)) {
            throw new RuntimeException("Category with name '" + categoryRequestDTO.getName() + "' already exists");
        }

        existingCategory.setName(categoryRequestDTO.getName());
        existingCategory.setDescription(categoryRequestDTO.getDescription());
        existingCategory.setDisplayOrder(categoryRequestDTO.getDisplayOrder() != null ?
                categoryRequestDTO.getDisplayOrder() : existingCategory.getDisplayOrder());

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());

        return mapToDTO(updatedCategory);
    }


    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // You might want to check if category has menu items before deleting
        // This would require a relationship with MenuItem

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }


    @Transactional(readOnly = true)
    public boolean categoryExistsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}