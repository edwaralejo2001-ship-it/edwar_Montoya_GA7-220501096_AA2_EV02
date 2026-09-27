package com.ewuarsoft.controller;

import com.ewuarsoft.model.*;
import com.ewuarsoft.service.CategoriaService;
import com.ewuarsoft.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listarProductos(@RequestParam(value = "q", required = false) String query,
                                  @RequestParam(value = "categoria", required = false) Long categoriaId,
                                  @RequestParam(value = "filtro", required = false) String filtro,
                                  HttpSession session,
                                  Model model) {

        if (filtro != null && filtro.equals("bajo_stock")) {
            model.addAttribute("productos", productoService.listarProductosBajoStock());
            model.addAttribute("filtroActivo", "bajo_stock");
        } else if (categoriaId != null && categoriaId > 0) {
            model.addAttribute("productos", productoService.listarPorCategoria(categoriaId));
            model.addAttribute("categoriaSeleccionada", categoriaId);
        } else if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("productos", productoService.buscar(query));
            model.addAttribute("busqueda", query);
        } else {
            model.addAttribute("productos", productoService.listarTodos());
        }

        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("totalProductos", productoService.contarProductos());
        model.addAttribute("stockCritico", productoService.contarProductosBajoStock());
        model.addAttribute("valorInventario", productoService.calcularValorTotalInventario());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "productos");

        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "productos");
        model.addAttribute("esEdicion", false);
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto,
                                  RedirectAttributes redirectAttributes,
                                  Model model,
                                  HttpSession session) {
        try {
            productoService.guardar(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto '" + producto.getNombre() + "' registrado correctamente en el inventario.");
            return "redirect:/productos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("paginaActiva", "productos");
            model.addAttribute("esEdicion", false);
            return "productos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Producto> productoOpt = productoService.buscarPorId(id);
        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Producto no encontrado.");
            return "redirect:/productos";
        }

        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "productos");
        model.addAttribute("esEdicion", true);
        return "productos/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarProducto(@ModelAttribute("producto") Producto producto,
                                     RedirectAttributes redirectAttributes,
                                     Model model,
                                     HttpSession session) {
        try {
            productoService.guardar(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto '" + producto.getNombre() + "' actualizado exitosamente.");
            return "redirect:/productos";
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error al actualizar el producto: " + e.getMessage());
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            model.addAttribute("paginaActiva", "productos");
            model.addAttribute("esEdicion", true);
            return "productos/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado del catálogo correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar el producto porque tiene movimientos o ventas asociadas.");
        }
        return "redirect:/productos";
    }

    @GetMapping("/movimiento")
    public String formularioMovimiento(@RequestParam(value = "productoId", required = false) Long productoId,
                                       HttpSession session,
                                       Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("productoSeleccionadoId", productoId);
        model.addAttribute("tiposMovimiento", TipoMovimiento.values());
        model.addAttribute("motivos", MotivoMovimiento.values());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "productos");
        return "productos/movimiento";
    }

    @PostMapping("/movimiento")
    public String procesarMovimiento(@RequestParam("productoId") Long productoId,
                                     @RequestParam("tipoMovimiento") TipoMovimiento tipoMovimiento,
                                     @RequestParam("motivo") MotivoMovimiento motivo,
                                     @RequestParam("cantidad") Integer cantidad,
                                     @RequestParam(value = "observacion", required = false) String observacion,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            String username = usuario != null ? usuario.getUsername() : "Admin";

            productoService.registrarMovimiento(productoId, tipoMovimiento, motivo, cantidad, username, observacion);
            redirectAttributes.addFlashAttribute("mensajeExito", "Movimiento de stock registrado satisfactoriamente.");
            return "redirect:/productos";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/productos/movimiento?productoId=" + productoId;
        }
    }
}
