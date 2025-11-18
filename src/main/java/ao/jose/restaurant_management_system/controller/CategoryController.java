package ao.jose.restaurant_management_system.controller;

import ao.jose.restaurant_management_system.dto.request.CategoryRequestDTO;
import ao.jose.restaurant_management_system.dto.response.CategoryResponseDTO;
import ao.jose.restaurant_management_system.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(
            @RequestParam(defaultValue = "false") boolean ordered) {

        List<CategoryResponseDTO> categories = ordered ?
                categoryService.getAllCategoriesOrdered() :
                categoryService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {

        CategoryResponseDTO createdCategory = categoryService.createCategory(categoryRequestDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {

        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryRequestDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> checkCategoryExists(@PathVariable String name) {
        boolean exists = categoryService.categoryExistsByName(name);
        return ResponseEntity.ok(exists);
    }
}