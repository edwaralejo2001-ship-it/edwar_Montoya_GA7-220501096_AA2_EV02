package com.ewuarsoft.controller;

import com.ewuarsoft.model.Cliente;
import com.ewuarsoft.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador para la Gestión y Directorio de Clientes.
 * Proyecto: EwuarSoft - Sistema de Gestión e Inventario.
 * Evidencia SENA: GA7-220501096-AA2-EV02.
 *
 * Módulo encargado de la administración de clientes:
 * - Consulta y listado con buscador por documento o razón social.
 * - Registro de nuevos clientes con validación de documento único.
 * - Modificación de datos de contacto y dirección.
 * - Eliminación de registros.
 *
 * @author Edwar Alejandro Montoya Ramírez
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Inyección del servicio de clientes.
     * @param clienteService Servicio con operaciones de persistencia y búsqueda.
     */
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Muestra el directorio de clientes con soporte para búsqueda interactiva.
     *
     * @param query Texto de búsqueda opcional por nombre o cédula.
     * @param session Sesión activa del usuario.
     * @param model Modelo para la plantilla Thymeleaf.
     * @return Vista templates/clientes/lista.html.
     */
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

    /**
     * Renderiza el formulario para añadir un nuevo cliente al sistema.
     *
     * @param session Sesión activa.
     * @param model Modelo con entidad vacía de Cliente.
     * @return Vista templates/clientes/formulario.html.
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "clientes");
        model.addAttribute("esEdicion", false);
        return "clientes/formulario";
    }

    /**
     * Guarda un nuevo cliente validando que el documento de identificación sea único.
     *
     * @param cliente Entidad cliente con los datos enviados en el formulario.
     * @param redirectAttributes Mensajes flash de notificación.
     * @param model Modelo para visualización de errores.
     * @param session Sesión HTTP.
     * @return Redirección a la lista o retorno al formulario en caso de duplicidad.
     */
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

    /**
     * Carga los datos del cliente solicitado para su modificación.
     *
     * @param id ID único del cliente.
     * @param session Sesión activa.
     * @param model Modelo para precargar los datos.
     * @param redirectAttributes Mensaje si el cliente no existe.
     * @return Vista de formulario en modo edición.
     */
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

    /**
     * Procesa la actualización de un cliente existente.
     *
     * @param cliente Datos modificados del cliente.
     * @param redirectAttributes Mensaje flash de éxito.
     * @param model Modelo de error.
     * @param session Sesión activa.
     * @return Redirección a la lista de clientes.
     */
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

    /**
     * Elimina un cliente siempre que no viole restricciones de clave foránea en ventas.
     *
     * @param id ID del cliente a remover.
     * @param redirectAttributes Mensaje con el resultado de la acción.
     * @return Redirección a la lista de clientes.
     */
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
