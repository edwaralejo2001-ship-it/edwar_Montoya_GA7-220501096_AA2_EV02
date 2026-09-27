package com.ewuarsoft.controller;

import com.ewuarsoft.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String verDashboard(HttpSession session, Model model) {
        Map<String, Object> stats = dashboardService.obtenerEstadisticas();
        model.addAllAttributes(stats);
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "dashboard");
        return "dashboard/index";
    }
}
