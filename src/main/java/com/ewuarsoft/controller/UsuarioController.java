package com.ewuarsoft.controller;

import com.ewuarsoft.model.EstadoUsuario;
import com.ewuarsoft.model.RolUsuario;
import com.ewuarsoft.model.Usuario;
import com.ewuarsoft.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador para la Administración de Usuarios y Accesos.
 * Proyecto: EwuarSoft - Sistema de Gestión e Inventario.
 * Evidencia SENA: GA7-220501096-AA2-EV02.
 *
 * Módulo de seguridad y administración:
 * - Listado de cuentas registradas con visualización de estado y rol.
 * - Creación de usuarios con roles (ADMINISTRADOR, CAJERO, ALMACENISTA).
 * - Modificación de perfiles y contraseñas.
 * - Alternancia rápida de estado (Activo/Inactivo).
 * - Eliminación con validación de seguridad contra auto-eliminación.
 *
 * @author Edwar Alejandro Montoya Ramírez
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Inyección del servicio de usuarios.
     * @param usuarioService Servicio de gestión de usuarios y autenticación.
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra la lista de todos los usuarios registrados en el sistema.
     *
     * @param session Sesión activa para verificar privilegios y avatar.
     * @param model Modelo con la lista de usuarios.
     * @return Vista templates/usuarios/lista.html.
     */
    @GetMapping
    public String listarUsuarios(HttpSession session, Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "usuarios");
        return "usuarios/lista";
    }

    /**
     * Formulario para registrar un nuevo usuario con selección de rol y estado.
     *
     * @param session Sesión activa.
     * @param model Modelo con enums de roles y estados.
     * @return Vista templates/usuarios/formulario.html.
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("estados", EstadoUsuario.values());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "usuarios");
        model.addAttribute("esEdicion", false);
        return "usuarios/formulario";
    }

    /**
     * Almacena un nuevo usuario en la base de datos MySQL.
     *
     * @param usuario Datos del nuevo usuario.
     * @param redirectAttributes Mensaje flash de notificación.
     * @param model Modelo para retornar en caso de error.
     * @param session Sesión activa.
     * @return Redirección a la lista o retorno a la vista de formulario.
     */
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpSession session) {
        try {
            usuarioService.registrar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario '" + usuario.getUsername() + "' registrado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("estados", EstadoUsuario.values());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("paginaActiva", "usuarios");
            model.addAttribute("esEdicion", false);
            return "usuarios/formulario";
        }
    }

    /**
     * Carga el usuario para edición de datos, rol o cambio de clave.
     *
     * @param id Identificador único del usuario.
     * @param session Sesión activa.
     * @param model Modelo de la vista.
     * @param redirectAttributes Mensaje si el usuario no existe.
     * @return Vista del formulario en modo edición.
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró el usuario solicitado.");
            return "redirect:/usuarios";
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(""); // Seguridad: No exponer la clave en el formulario

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("estados", EstadoUsuario.values());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "usuarios");
        model.addAttribute("esEdicion", true);
        return "usuarios/formulario";
    }

    /**
     * Procesa la actualización de un usuario existente.
     *
     * @param usuario Datos actualizados.
     * @param redirectAttributes Mensaje flash de éxito.
     * @param model Modelo para captura de errores.
     * @param session Sesión activa.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/actualizar")
    public String actualizarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                    RedirectAttributes redirectAttributes,
                                    Model model,
                                    HttpSession session) {
        try {
            usuarioService.actualizar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario '" + usuario.getUsername() + "' actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error al actualizar el usuario: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("estados", EstadoUsuario.values());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("paginaActiva", "usuarios");
            model.addAttribute("esEdicion", true);
            return "usuarios/formulario";
        }
    }

    /**
     * Alterna rápidamente el estado del usuario entre ACTIVO e INACTIVO.
     * Aplica la regla de seguridad que impide desactivar la cuenta del usuario en sesión actual.
     *
     * @param id Identificador del usuario.
     * @param session Sesión activa.
     * @param redirectAttributes Mensaje flash informativo.
     * @return Redirección a la lista de usuarios.
     */
    @GetMapping("/cambiar-estado/{id}")
    public String alternarEstado(@PathVariable("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario logueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (logueado != null && logueado.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("mensajeError", "No puedes desactivar tu propia cuenta en sesión.");
            return "redirect:/usuarios";
        }

        usuarioService.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Estado de usuario modificado con éxito.");
        return "redirect:/usuarios";
    }

    /**
     * Elimina un usuario del sistema, protegiendo al usuario en sesión contra auto-eliminación.
     *
     * @param id Identificador del usuario a eliminar.
     * @param session Sesión activa.
     * @param redirectAttributes Mensaje flash con el resultado.
     * @return Redirección a la lista de usuarios.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario logueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (logueado != null && logueado.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("mensajeError", "No puedes eliminar tu propio usuario mientras estás conectado.");
            return "redirect:/usuarios";
        }

        try {
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado satisfactoriamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
