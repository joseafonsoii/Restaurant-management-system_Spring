package ao.jose.restaurant_management_system.controller.web;

import ao.jose.restaurant_management_system.dto.request.OrderItemRequestDTO;
import ao.jose.restaurant_management_system.dto.request.OrderRequestDTO;
import ao.jose.restaurant_management_system.dto.response.MenuItemResponseDTO;
import ao.jose.restaurant_management_system.dto.response.OrderResponseDTO;
import ao.jose.restaurant_management_system.dto.response.RestaurantTableResponseDTO;
import ao.jose.restaurant_management_system.service.MenuItemService;
import ao.jose.restaurant_management_system.service.OrderService;
import ao.jose.restaurant_management_system.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderWebController {

    private final OrderService orderService;
    private final RestaurantTableService tableService;
    private final MenuItemService menuItemService;

    @GetMapping
    public String list(Model model) {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        OrderResponseDTO order = orderService.getOrderById(id);
        List<MenuItemResponseDTO> availableMenuItems = menuItemService.getAvailableMenuItems();
        model.addAttribute("order", order);
        model.addAttribute("availableMenuItems", availableMenuItems);
        return "orders/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("order", new OrderRequestDTO());
        model.addAttribute("availableTables", tableService.getAvailableTables());
        return "orders/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute OrderRequestDTO orderRequestDTO, RedirectAttributes redirectAttributes) {
        try {
            OrderResponseDTO order = orderService.createOrder(orderRequestDTO);
            redirectAttributes.addFlashAttribute("success", "Pedido criado com sucesso!");
            return "redirect:/orders/" + order.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/new";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Status atualizado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pedido cancelado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/add-item")
    public String addItem(@PathVariable Long id,
                          @RequestParam Long menuItemId,
                          @RequestParam Integer quantity,
                          @RequestParam(required = false) String notes,
                          RedirectAttributes redirectAttributes) {
        try {
            OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                    .menuItemId(menuItemId)
                    .quantity(quantity)
                    .notes(notes)
                    .build();
            orderService.addItemToOrder(id, itemRequest);
            redirectAttributes.addFlashAttribute("success", "Item adicionado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pedido excluido com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }
}
