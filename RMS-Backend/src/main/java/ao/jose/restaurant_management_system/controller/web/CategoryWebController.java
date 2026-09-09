package ao.jose.restaurant_management_system.controller.web;

import ao.jose.restaurant_management_system.dto.request.CategoryRequestDTO;
import ao.jose.restaurant_management_system.dto.response.CategoryResponseDTO;
import ao.jose.restaurant_management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryWebController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        List<CategoryResponseDTO> categories = categoryService.getAllCategoriesOrdered();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new CategoryRequestDTO());
        return "categories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        CategoryRequestDTO dto = CategoryRequestDTO.builder()
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .build();
        model.addAttribute("category", dto);
        model.addAttribute("categoryId", id);
        return "categories/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CategoryRequestDTO categoryRequestDTO,
                       @RequestParam(value = "id", required = false) Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            if (id != null) {
                categoryService.updateCategory(id, categoryRequestDTO);
                redirectAttributes.addFlashAttribute("success", "Categoria atualizada com sucesso!");
            } else {
                categoryService.createCategory(categoryRequestDTO);
                redirectAttributes.addFlashAttribute("success", "Categoria criada com sucesso!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (id != null) {
                return "redirect:/categories/" + id + "/edit";
            }
            return "redirect:/categories/new";
        }
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Categoria excluida com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
}
