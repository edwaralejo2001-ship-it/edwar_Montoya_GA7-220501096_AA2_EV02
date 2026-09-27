package com.ewuarsoft.controller;

import com.ewuarsoft.model.Cliente;
import com.ewuarsoft.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarClientes(@RequestParam(value = "q", required = false) String query,
                                 HttpSession session,
                                 Model model) {
        model.addAttribute("clientes", clienteService.buscar(query));
        model.addAttribute("busqueda", query != null ? query : "");
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "clientes");
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "clientes");
        model.addAttribute("esEdicion", false);
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpSession session) {
        try {
            clienteService.guardar(cliente);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente '" + cliente.getNombre() + "' registrado correctamente.");
            return "redirect:/clientes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("paginaActiva", "clientes");
            model.addAttribute("esEdicion", false);
            return "clientes/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOpt = clienteService.buscarPorId(id);
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Cliente no encontrado.");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "clientes");
        model.addAttribute("esEdicion", true);
        return "clientes/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarCliente(@ModelAttribute("cliente") Cliente cliente,
                                    RedirectAttributes redirectAttributes,
                                    Model model,
                                    HttpSession session) {
        try {
            clienteService.guardar(cliente);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente '" + cliente.getNombre() + "' actualizado correctamente.");
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error al actualizar el cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("paginaActiva", "clientes");
            model.addAttribute("esEdicion", true);
            return "clientes/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente eliminado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar el cliente porque tiene ventas o registros asociados.");
        }
        return "redirect:/clientes";
    }
}
