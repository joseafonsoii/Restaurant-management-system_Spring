package ao.jose.restaurant_management_system.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class WebModelAdvice {

    @ModelAttribute
    public void addActiveSection(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        String section = "dashboard";
        if (uri.startsWith("/categories")) {
            section = "categories";
        } else if (uri.startsWith("/menu-items")) {
            section = "menu-items";
        } else if (uri.startsWith("/tables")) {
            section = "tables";
        } else if (uri.startsWith("/orders")) {
            section = "orders";
        } else if (uri.startsWith("/payments")) {
            section = "payments";
        }
        model.addAttribute("activeSection", section);
    }
}