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

/**
 * Controlador de Productos e Inventario.
 * Proyecto: EwuarSoft - Sistema de Gestión e Inventario.
 * Evidencia SENA: GA7-220501096-AA2-EV02.
 *
 * Implementa las operaciones del módulo de inventario:
 * - Catálogo de productos con indicadores de stock.
 * - Filtros por categoría y búsqueda textual por nombre/código.
 * - Operaciones CRUD (Crear, Leer, Actualizar, Eliminar).
 * - Registro de movimientos de inventario (Entradas y Salidas de existencias).
 *
 * @author Edwar Alejandro Montoya Ramírez
 */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    /**
     * Constructor con inyección de dependencias para los servicios de negocio requeridos.
     * @param productoService Servicio para lógica de inventario y stock.
     * @param categoriaService Servicio para consulta de categorías.
     */
    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    /**
     * Lista los productos del inventario permitiendo búsqueda, filtros por categoría y filtros de stock crítico.
     *
     * @param query Término de búsqueda opcional (nombre o código SKU).
     * @param categoriaId Identificador opcional de categoría para filtrado.
     * @param filtro Criterio especial de filtrado (ej. 'bajo_stock').
     * @param session Sesión HTTP para verificar el usuario conectado.
     * @param model Modelo para abastecer la vista Thymeleaf.
     * @return Vista templates/productos/lista.html.
     */
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

        // Carga de datos complementarios para filtros y badges de estado
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("totalProductos", productoService.contarProductos());
        model.addAttribute("stockCritico", productoService.contarProductosBajoStock());
        model.addAttribute("valorInventario", productoService.calcularValorTotalInventario());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "productos");

        return "productos/lista";
    }

    /**
     * Muestra el formulario para registrar un nuevo producto en el catálogo.
     *
     * @param session Sesión HTTP actual.
     * @param model Modelo con instancia vacía de Producto y lista de categorías disponibles.
     * @return Vista templates/productos/formulario.html.
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("paginaActiva", "productos");
        model.addAttribute("esEdicion", false);
        return "productos/formulario";
    }

    /**
     * Procesa y almacena un nuevo producto en la base de datos MySQL vía Hibernate.
     *
     * @param producto Entidad producto con los datos ingresados en el formulario.
     * @param redirectAttributes Atributos flash para mensaje de confirmación.
     * @param model Modelo para recarga en caso de error.
     * @param session Sesión HTTP.
     * @return Redirección a la lista o retorno al formulario si existen fallos de validación.
     */
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

    /**
     * Carga el producto por su identificador primario para edición.
     *
     * @param id Identificador numérico del producto.
     * @param session Sesión HTTP actual.
     * @param model Modelo para poblar campos en la vista.
     * @param redirectAttributes Mensajes flash en caso de no encontrar el producto.
     * @return Vista templates/productos/formulario.html configurada en modo edición.
     */
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

    /**
     * Procesa la actualización de los datos de un producto existente.
     *
     * @param producto Entidad con los datos modificados.
     * @param redirectAttributes Mensaje flash de éxito.
     * @param model Modelo para errores.
     * @param session Sesión HTTP.
     * @return Redirección a la lista de productos.
     */
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

    /**
     * Elimina un producto del catálogo respetando la integridad referencial.
     *
     * @param id Identificador del producto a eliminar.
     * @param redirectAttributes Mensaje de confirmación o alerta si tiene registros vinculados.
     * @return Redirección al catálogo.
     */
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

    /**
     * Muestra la interfaz para registrar movimientos de existencias (entradas/salidas).
     *
     * @param productoId ID opcional para seleccionar automáticamente un producto específico.
     * @param session Sesión de usuario actual.
     * @param model Modelo para abastecer el listado de productos, tipos y motivos de movimiento.
     * @return Vista templates/productos/movimiento.html.
     */
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

    /**
     * Procesa la entrada o salida de inventario, actualiza las existencias físicas y guarda la auditoría.
     *
     * @param productoId Identificador del producto afectado.
     * @param tipoMovimiento ENTRADA o SALIDA de inventario.
     * @param motivo Justificación del movimiento (COMPRA, VENTA, MERMA_DANO, AJUSTE, etc.).
     * @param cantidad Cantidad física a incrementar o descontar.
     * @param observacion Nota descriptiva opcional.
     * @param session Sesión activa para tomar el usuario responsable.
     * @param redirectAttributes Mensaje flash de respuesta.
     * @param model Modelo de retorno.
     * @return Redirección al catálogo si es exitoso o retorno al formulario en caso de insuficiencia de stock.
     */
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
