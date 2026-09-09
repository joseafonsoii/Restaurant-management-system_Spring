package ao.jose.restaurant_management_system.controller.web;

import ao.jose.restaurant_management_system.dto.request.RestaurantTableRequestDTO;
import ao.jose.restaurant_management_system.dto.response.RestaurantTableResponseDTO;
import ao.jose.restaurant_management_system.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableWebController {

    private final RestaurantTableService tableService;

    @GetMapping
    public String list(Model model) {
        List<RestaurantTableResponseDTO> tables = tableService.getAllTables();
        long availableCount = tables.stream().filter(t -> t.getStatus().name().equals("AVAILABLE")).count();
        long occupiedCount = tables.stream().filter(t -> t.getStatus().name().equals("OCCUPIED")).count();
        long reservedCount = tables.stream().filter(t -> t.getStatus().name().equals("RESERVED")).count();

        model.addAttribute("tables", tables);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("occupiedCount", occupiedCount);
        model.addAttribute("reservedCount", reservedCount);
        model.addAttribute("totalTables", tables.size());
        return "tables/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("table", new RestaurantTableRequestDTO());
        return "tables/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        RestaurantTableResponseDTO table = tableService.getTableById(id);
        RestaurantTableRequestDTO dto = RestaurantTableRequestDTO.builder()
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .build();
        model.addAttribute("table", dto);
        model.addAttribute("tableId", id);
        return "tables/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute RestaurantTableRequestDTO tableRequestDTO,
                       @RequestParam(value = "id", required = false) Long id,
                       RedirectAttributes redirectAttributes) {
        try {
            if (id != null) {
                tableService.updateTable(id, tableRequestDTO);
                redirectAttributes.addFlashAttribute("success", "Mesa atualizada com sucesso!");
            } else {
                tableService.createTable(tableRequestDTO);
                redirectAttributes.addFlashAttribute("success", "Mesa criada com sucesso!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (id != null) {
                return "redirect:/tables/" + id + "/edit";
            }
            return "redirect:/tables/new";
        }
        return "redirect:/tables";
    }

    @PostMapping("/{id}/occupy")
    public String occupy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tableService.occupyTable(id);
            redirectAttributes.addFlashAttribute("success", "Mesa ocupada com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tables";
    }

    @PostMapping("/{id}/free")
    public String free(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tableService.freeTable(id);
            redirectAttributes.addFlashAttribute("success", "Mesa liberada com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tables";
    }

    @PostMapping("/{id}/reserve")
    public String reserve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tableService.reserveTable(id);
            redirectAttributes.addFlashAttribute("success", "Mesa reservada com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tables";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tableService.deleteTable(id);
            redirectAttributes.addFlashAttribute("success", "Mesa excluida com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tables";
    }
}
