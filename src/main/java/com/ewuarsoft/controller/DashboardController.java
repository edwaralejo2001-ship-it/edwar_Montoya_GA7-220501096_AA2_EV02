package com.ewuarsoft.controller;

import com.ewuarsoft.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * Controlador del Panel de Control (Dashboard Principal).
 * Proyecto: EwuarSoft - Sistema de Gestión e Inventario.
 * Evidencia SENA: GA7-220501096-AA2-EV02.
 *
 * Consolida la información gerencial y operativa de la plataforma:
 * - Indicadores clave de rendimiento (KPIs): productos, clientes, usuarios, valor del inventario.
 * - Monitoreo en tiempo real de productos con stock crítico/bajo.
 * - Enlaces rápidos a las operaciones más comunes.
 *
 * @author Edwar Alejandro Montoya Ramírez
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Inyección del servicio de estadísticas del dashboard.
     * @param dashboardService Servicio de métricas agregadas.
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Redirección de la raíz del servidor al panel de control principal.
     * @return Redirección a /dashboard.
     */
    @GetMapping("/")
    public String inicio() {
        return "redirect:/dashboard";
    }

    /**
     * Renderiza la vista del panel de control con métricas agregadas de Hibernate.
     *
     * @param session Sesión activa para obtener el perfil del usuario logueado.
     * @param model Modelo para abastecer las tarjetas y tablas de la plantilla.
     * @return Vista templates/dashboard/index.html.
     */
    @GetMapping("/dashboard")
    public String verDashboard(HttpSession session, Model model) {
        Map<String, Object> stats = dashboardService.obtenerEstadisticas();
        model.addAllAttributes(stats);
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "dashboard");
        return "dashboard/index";
    }
}
