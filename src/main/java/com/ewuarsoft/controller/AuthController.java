package com.ewuarsoft.controller;

import com.ewuarsoft.model.EstadoUsuario;
import com.ewuarsoft.model.RolUsuario;
import com.ewuarsoft.model.Usuario;
import com.ewuarsoft.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String verLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "registrado", required = false) String registrado,
                           HttpSession session,
                           Model model) {

        // Si ya tiene sesión activa, redirigir directo al dashboard
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }

        if ("credenciales".equals(error)) {
            model.addAttribute("mensajeError", "Usuario o contraseña incorrectos, o usuario inactivo.");
        } else if ("requiere_sesion".equals(error)) {
            model.addAttribute("mensajeError", "Debe iniciar sesión para acceder a la plataforma.");
        }

        if (logout != null) {
            model.addAttribute("mensajeExito", "Has cerrado sesión correctamente.");
        }
        if (registrado != null) {
            model.addAttribute("mensajeExito", "Usuario registrado exitosamente. Ahora puedes iniciar sesión.");
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.autenticar(username.trim(), password.trim());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addAttribute("error", "credenciales");
            return "redirect:/login";
        }
    }

    @GetMapping("/registro")
    public String verRegistro(HttpSession session, Model model) {
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(Usuario nuevoUsuario,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            if (nuevoUsuario.getUsername() == null || nuevoUsuario.getUsername().trim().isEmpty() ||
                nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().trim().isEmpty() ||
                nuevoUsuario.getNombreCompleto() == null || nuevoUsuario.getNombreCompleto().trim().isEmpty()) {
                model.addAttribute("mensajeError", "Todos los campos obligatorios deben ser diligenciados.");
                model.addAttribute("usuario", nuevoUsuario);
                return "auth/registro";
            }

            nuevoUsuario.setUsername(nuevoUsuario.getUsername().trim());
            nuevoUsuario.setRol(RolUsuario.CAJERO);
            nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);

            usuarioService.registrar(nuevoUsuario);
            redirectAttributes.addAttribute("registrado", "exito");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("usuario", nuevoUsuario);
            return "auth/registro";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addAttribute("logout", "exito");
        return "redirect:/login";
    }
}
