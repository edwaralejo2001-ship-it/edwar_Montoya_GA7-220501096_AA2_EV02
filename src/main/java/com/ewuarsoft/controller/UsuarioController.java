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

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(HttpSession session, Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "usuarios");
        return "usuarios/lista";
    }

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
        usuario.setPassword(""); // No enviar la contraseña al formulario por seguridad

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("estados", EstadoUsuario.values());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "usuarios");
        model.addAttribute("esEdicion", true);
        return "usuarios/formulario";
    }

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
