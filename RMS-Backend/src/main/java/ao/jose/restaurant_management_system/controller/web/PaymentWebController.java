package ao.jose.restaurant_management_system.controller.web;

import ao.jose.restaurant_management_system.dto.request.PaymentRequestDTO;
import ao.jose.restaurant_management_system.dto.response.OrderResponseDTO;
import ao.jose.restaurant_management_system.dto.response.PaymentResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.PaymentStatisticsDTO;
import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import ao.jose.restaurant_management_system.service.OrderService;
import ao.jose.restaurant_management_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentWebController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @GetMapping
    public String list(Model model) {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        PaymentStatisticsDTO stats = paymentService.getPaymentStatistics();

        model.addAttribute("payments", payments);
        model.addAttribute("totalRevenue", stats.getTotalRevenue());
        model.addAttribute("todayRevenue", stats.getTodayRevenue());
        model.addAttribute("totalTransactions", stats.getTotalTransactions());
        model.addAttribute("failedTransactions", stats.getFailedTransactions());
        return "payments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        List<OrderResponseDTO> unpaidOrders = orderService.getAllOrders().stream()
                .filter(o -> o.getStatus() != OrderStatus.PAID && o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());
        model.addAttribute("payment", new PaymentRequestDTO());
        model.addAttribute("unpaidOrders", unpaidOrders);
        return "payments/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute PaymentRequestDTO paymentRequestDTO, RedirectAttributes redirectAttributes) {
        try {
            paymentService.createPayment(paymentRequestDTO);
            redirectAttributes.addFlashAttribute("success", "Pagamento criado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/payments/new";
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/process")
    public String process(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.processPayment(id);
            redirectAttributes.addFlashAttribute("success", "Pagamento processado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/refund")
    public String refund(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.refundPayment(id);
            redirectAttributes.addFlashAttribute("success", "Pagamento reembolsado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("success", "Pagamento excluido com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments";
    }
}
