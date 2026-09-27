# EwuarSoft - Plataforma de Gestión e Inventario

**Evidencia SENA:** `GA7-220501096-AA2-EV02 - Codificación de módulos del software utilizando el lenguaje de programación seleccionado y framework`  
**Autor:** Edwar Montoya  
**Repositorio GitHub:** [https://github.com/edwaralejo2001-ship-it/edwar_Montoya_GA7-220501096_AA2_EV02.git](https://github.com/edwaralejo2001-ship-it/edwar_Montoya_GA7-220501096_AA2_EV02.git)

---

## 📌 Descripción del Proyecto

**EwuarSoft** es una aplicación web empresarial orientada a la gestión comercial, control de existencias e inventario, desarrollada bajo una arquitectura limpia por capas utilizando el ecosistema de **Spring Boot 3** y persistencia relacional con **Hibernate ORM (Spring Data JPA)**. Cuenta con una base de datos propia en **XAMPP MySQL** (`ewuarsoft_app_db`) y un sistema de interfaz visual **CSS+** moderno, responsivo y dinámico.

---

## 🚀 Tecnologías y Herramientas

- **Backend:** Java 21 / 25, Spring Boot 3.3.4 (Spring Web MVC, Spring Data JPA, Hibernate, Bean Validation).
- **Frontend (CSS+):** Thymeleaf, HTML5 semántico, Bootstrap 5, Bootstrap Icons, Google Fonts (Plus Jakarta Sans & Outfit) y hoja de estilos personalizada `style.css` con diseño glassmorphism, micro-interacciones hover y variables CSS.
- **Base de Datos:** MySQL / MariaDB (XAMPP), base de datos `ewuarsoft_app_db`.
- **Control de Versiones:** Git & GitHub.
- **Gestión de Dependencias y Construcción:** Apache Maven.

---

## 🔐 Credenciales de Acceso al Sistema

Para ingresar al sistema a través de `/login`:

| Usuario | Contraseña | Rol | Acceso |
| :--- | :--- | :--- | :--- |
| **admin** | `admin123` | **ADMINISTRADOR** | Acceso total al dashboard, usuarios, clientes y productos |
| **cajero** | `cajero123` | **CAJERO** | Gestión de clientes y catálogo de productos |
| **almacen** | `almacen123` | **ALMACENISTA** | Control de stock y movimientos de inventario |

> También se encuentra disponible el módulo de **Registro de Nuevos Usuarios** en `/registro`.

---

## 📦 Módulos Principales Implementados

1. **Módulo de Autenticación y Sesión:**
   - Inicio de sesión (`/login`) con validación de credenciales activas.
   - Registro público de nuevos usuarios (`/registro`).
   - Cierre de sesión seguro (`/logout`).
   - Interceptor `AuthInterceptor` para protección de rutas y control de sesión en memoria (`HttpSession`).

2. **Dashboard Analítico (`/dashboard`):**
   - Tarjetas KPI: Cantidad total de productos, clientes registrados, usuarios y valor monetario del inventario.
   - Semáforo y tabla de **Alertas de Reabastecimiento** para productos donde el stock actual sea inferior o igual al stock mínimo.
   - Accesos directos para agilizar operaciones comunes.

3. **Módulo de Gestión de Clientes (`/clientes`):**
   - Directorio y tabla interactiva de clientes.
   - Buscador por nombre o cédula/documento.
   - Registro de nuevos clientes con validación de documento único.
   - Edición y actualización de datos de contacto.
   - Eliminación con validación de integridad referencial.

4. **Módulo de Gestión de Usuarios (`/usuarios`):**
   - Administración de credenciales y roles (`ADMINISTRADOR`, `CAJERO`, `ALMACENISTA`).
   - Activación y desactivación rápida de usuarios con un clic.
   - Protección contra auto-eliminación o auto-desactivación del usuario en sesión activa.
   - Creación y edición de cuentas.

5. **Módulo de Productos e Inventario (`/productos`):**
   - Catálogo de productos con código/SKU, nombre, categoría, precios y stock.
   - Indicadores dinámicos de estado: 🟢 *Óptimo*, 🟡 *Stock Bajo*, 🔴 *Agotado*.
   - Filtro por categorías y buscador en tiempo real.
   - Registro de entradas (compras a proveedores) y salidas (ventas, mermas o ajustes físicos) con cálculo automático de existencias e historial de auditoría.

---

## 🗄️ Modelo Relacional y Entidades Hibernate

El proyecto mapea con anotaciones Hibernate (`@Entity`, `@Table`, `@ManyToOne`, `@OneToMany`, `@Enumerated`) las siguientes tablas:

- **`Usuario`** ➔ `usuarios`
- **`Categoria`** ➔ `categorias`
- **`Proveedor`** ➔ `proveedores`
- **`Cliente`** ➔ `clientes`
- **`Producto`** ➔ `productos` (Relación `@ManyToOne` con `Categoria`)
- **`Venta`** ➔ `ventas` (Relación `@ManyToOne` con `Cliente`, `@OneToMany` con `DetalleVenta`)
- **`DetalleVenta`** ➔ `detalles_venta` (Relaciones con `Venta` y `Producto`)
- **`MovimientoInventario`** ➔ `movimientos_inventario` (Relación con `Producto` y registro de auditoría)

---

## ⚙️ Instrucciones de Ejecución

1. **Verificar XAMPP:**
   - Asegurarse de que el servicio **MySQL** esté iniciado en XAMPP (puerto `3306`).
   - La base de datos `ewuarsoft_app_db` ya se encuentra creada e inicializada con el archivo `database.sql`.

2. **Ejecutar el Proyecto:**
   - Desde PowerShell o CMD en la carpeta del proyecto:
     ```bash
     .\run.bat
     ```
   - O directamente con Maven:
     ```bash
     mvn spring-boot:run
     ```

3. **Abrir en el Navegador:**
   - Acceder a: [http://localhost:8080](http://localhost:8080)
