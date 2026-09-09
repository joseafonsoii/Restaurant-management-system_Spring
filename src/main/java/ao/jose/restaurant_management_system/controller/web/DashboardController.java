package ao.jose.restaurant_management_system.controller.web;

import ao.jose.restaurant_management_system.dto.response.OrderResponseDTO;
import ao.jose.restaurant_management_system.dto.response.RestaurantTableResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.OrderStatisticsDTO;
import ao.jose.restaurant_management_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService;
    private final RestaurantTableService tableService;
    private final PaymentService paymentService;
    private final CategoryService categoryService;
    private final MenuItemService menuItemService;

    @GetMapping("/")
    public String dashboard(Model model) {
        OrderStatisticsDTO orderStats = orderService.getOrderStatistics();
        List<OrderResponseDTO> activeOrders = orderService.getActiveOrders();
        List<RestaurantTableResponseDTO> tables = tableService.getAllTables();

        long availableTables = tables.stream()
                .filter(t -> t.getStatus().name().equals("AVAILABLE"))
                .count();

        model.addAttribute("totalOrders", orderStats.getTotalOrders());
        model.addAttribute("activeOrders", orderStats.getActiveOrders());
        model.addAttribute("dailyRevenue", orderStats.getDailyRevenue());
        model.addAttribute("availableTables", availableTables);
        model.addAttribute("activeOrdersList", activeOrders);
        model.addAttribute("tablesList", tables);

        return "dashboard";
    }
}
