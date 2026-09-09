package ao.jose.restaurant_management_system.controller.web;

import ao.jose.restaurant_management_system.dto.request.MenuItemRequestDTO;
import ao.jose.restaurant_management_system.dto.response.CategoryResponseDTO;
import ao.jose.restaurant_management_system.dto.response.MenuItemResponseDTO;
import ao.jose.restaurant_management_system.service.CategoryService;
import ao.jose.restaurant_management_system.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/menu-items")
@RequiredArgsConstructor
public class MenuItemWebController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        List<MenuItemResponseDTO> menuItems = menuItemService.getAllMenuItems();
        model.addAttribute("menuItems", menuItems);
        return "menu-items/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("menuItem", new MenuItemRequestDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "menu-items/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MenuItemResponseDTO item = menuItemService.getMenuItemById(id);
        MenuItemRequestDTO dto = MenuItemRequestDTO.builder()
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategory().getId())
                .preparationTime(item.getPreparationTime())
                .available(item.getAvailable())
                .ingredients(item.getIngredients())
                .tags(item.getTags())
                .build();
        model.addAttribute("menuItem", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("itemId", id);
        return "menu-items/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MenuItemRequestDTO menuItemRequestDTO,
                       @RequestParam(value = "id", required = false) Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            if (id != null) {
                menuItemService.updateMenuItem(id, menuItemRequestDTO);
                redirectAttributes.addFlashAttribute("success", "Item atualizado com sucesso!");
            } else {
                menuItemService.createMenuItem(menuItemRequestDTO);
                redirectAttributes.addFlashAttribute("success", "Item criado com sucesso!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (id != null) {
                return "redirect:/menu-items/" + id + "/edit";
            }
            return "redirect:/menu-items/new";
        }
        return "redirect:/menu-items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            menuItemService.deleteMenuItem(id);
            redirectAttributes.addFlashAttribute("success", "Item excluido com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/menu-items";
    }
}
