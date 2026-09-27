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

/**
 * Controlador de Autenticación y Control de Acceso.
 * Proyecto: EwuarSoft - Sistema de Gestión e Inventario.
 * Evidencia SENA: GA7-220501096-AA2-EV02.
 *
 * Gestiona el ciclo de vida de la sesión de usuario:
 * - Visualización y procesamiento del inicio de sesión (Login).
 * - Registro público de nuevos usuarios.
 * - Cierre de sesión e invalidación de credenciales (Logout).
 *
 * @author Edwar Alejandro Montoya Ramírez
 */
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    /**
     * Inyección de dependencias mediante constructor para desacoplamiento y testabilidad.
     * @param usuarioService Servicio de lógica de negocio de usuarios.
     */
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra la vista de inicio de sesión.
     * Si el usuario ya cuenta con una sesión HTTP activa, se redirige inmediatamente al dashboard.
     *
     * @param error Código de error opcional (credenciales incorrectas o requerimiento de sesión).
     * @param logout Parámetro opcional para confirmar que se cerró la sesión satisfactoriamente.
     * @param registrado Parámetro opcional para confirmar que un nuevo usuario se registró con éxito.
     * @param session Sesión HTTP actual.
     * @param model Modelo para enviar mensajes de alerta a la vista Thymeleaf.
     * @return Nombre de la plantilla de login o redirección al dashboard.
     */
    @GetMapping("/login")
    public String verLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "registrado", required = false) String registrado,
                           HttpSession session,
                           Model model) {

        // Validar si el usuario ya se encuentra autenticado
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }

        // Manejo de mensajes de error de autenticación
        if ("credenciales".equals(error)) {
            model.addAttribute("mensajeError", "Usuario o contraseña incorrectos, o cuenta inactiva.");
        } else if ("requiere_sesion".equals(error)) {
            model.addAttribute("mensajeError", "Debe iniciar sesión para acceder a las funcionalidades del sistema.");
        }

        // Manejo de mensajes de confirmación
        if (logout != null) {
            model.addAttribute("mensajeExito", "Has cerrado sesión correctamente.");
        }
        if (registrado != null) {
            model.addAttribute("mensajeExito", "Usuario registrado exitosamente. Ahora puedes iniciar sesión.");
        }

        return "auth/login";
    }

    /**
     * Procesa las credenciales enviadas por el formulario de inicio de sesión.
     *
     * @param username Nombre de usuario ingresado.
     * @param password Contraseña en texto claro para su verificación.
     * @param session Objeto HttpSession donde se almacenará el objeto de usuario si es válido.
     * @param redirectAttributes Atributos de redirección para mensajes flash.
     * @return Redirección a /dashboard en caso de éxito o a /login en caso de error.
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.autenticar(username.trim(), password.trim());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Almacenar el usuario autenticado en la sesión del servidor
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addAttribute("error", "credenciales");
            return "redirect:/login";
        }
    }

    /**
     * Muestra la interfaz de registro de nuevos usuarios.
     *
     * @param session Sesión HTTP actual.
     * @param model Modelo para inicializar la entidad Usuario vinculada al formulario.
     * @return Plantilla auth/registro.html.
     */
    @GetMapping("/registro")
    public String verRegistro(HttpSession session, Model model) {
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    /**
     * Procesa la creación de un nuevo usuario en la plataforma.
     * Aplica validaciones de negocio: campos requeridos, rol por defecto (CAJERO) y estado inicial (ACTIVO).
     *
     * @param nuevoUsuario Entidad usuario construida desde el formulario.
     * @param redirectAttributes Atributos flash para confirmación.
     * @param model Modelo de la vista para retornar errores sin perder datos digitados.
     * @return Redirección a login tras creación o retorno a registro si ocurren errores.
     */
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

    /**
     * Cierra la sesión activa en el servidor e invalida la cookie JSESSIONID.
     *
     * @param session Sesión HTTP actual a invalidar.
     * @param redirectAttributes Atributos de redirección para notificación de salida.
     * @return Redirección a /login?logout=exito.
     */
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addAttribute("logout", "exito");
        return "redirect:/login";
    }
}
