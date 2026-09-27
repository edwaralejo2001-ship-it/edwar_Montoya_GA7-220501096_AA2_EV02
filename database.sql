-- ==============================================================
-- BASE DE DATOS: ewuarsoft_app_db
-- Sistema de Inventario y Gestión EwuarSoft
-- SENA - Guía de Aprendizaje 7 - GA7-220501096-AA2-EV02
-- ==============================================================

CREATE DATABASE IF NOT EXISTS `ewuarsoft_app_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ewuarsoft_app_db`;

-- Desactivar chequeo de llaves foráneas para reinicialización limpia
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `movimientos_inventario`;
DROP TABLE IF EXISTS `detalles_venta`;
DROP TABLE IF EXISTS `ventas`;
DROP TABLE IF EXISTS `productos`;
DROP TABLE IF EXISTS `proveedores`;
DROP TABLE IF EXISTS `clientes`;
DROP TABLE IF EXISTS `categorias`;
DROP TABLE IF EXISTS `usuarios`;

SET FOREIGN_KEY_CHECKS = 1;

-- -------------------------------------------------------------
-- 1. TABLA: usuarios
-- -------------------------------------------------------------
CREATE TABLE `usuarios` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nombre_completo` VARCHAR(150) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `rol` ENUM('ADMINISTRADOR', 'CAJERO', 'ALMACENISTA') NOT NULL DEFAULT 'CAJERO',
  `estado` ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 2. TABLA: categorias
-- -------------------------------------------------------------
CREATE TABLE `categorias` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categoria_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 3. TABLA: proveedores
-- -------------------------------------------------------------
CREATE TABLE `proveedores` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nit` VARCHAR(30) NOT NULL,
  `razon_social` VARCHAR(150) NOT NULL,
  `contacto` VARCHAR(100) DEFAULT NULL,
  `telefono` VARCHAR(30) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `direccion` VARCHAR(200) DEFAULT NULL,
  `categoria_suministro` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_proveedor_nit` (`nit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 4. TABLA: clientes
-- -------------------------------------------------------------
CREATE TABLE `clientes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `documento` VARCHAR(30) NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `telefono` VARCHAR(30) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `direccion` VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cliente_documento` (`documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 5. TABLA: productos
-- -------------------------------------------------------------
CREATE TABLE `productos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `codigo` VARCHAR(30) NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `categoria_id` BIGINT NOT NULL,
  `precio_compra` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `precio_venta` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `stock_actual` INT NOT NULL DEFAULT 0,
  `stock_minimo` INT NOT NULL DEFAULT 5,
  `unidad_medida` VARCHAR(50) NOT NULL DEFAULT 'Unidad',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_producto_codigo` (`codigo`),
  KEY `fk_producto_categoria` (`categoria_id`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 6. TABLA: ventas
-- -------------------------------------------------------------
CREATE TABLE `ventas` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `numero_factura` VARCHAR(30) NOT NULL,
  `fecha_hora` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `cliente_id` BIGINT NOT NULL,
  `metodo_pago` ENUM('EFECTIVO', 'TRANSFERENCIA_NEQUI', 'TARJETA_DEBITO', 'TARJETA_CREDITO') NOT NULL DEFAULT 'EFECTIVO',
  `total` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `usuario` VARCHAR(100) NOT NULL,
  `notas` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_venta_factura` (`numero_factura`),
  KEY `fk_venta_cliente` (`cliente_id`),
  CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 7. TABLA: detalles_venta
-- -------------------------------------------------------------
CREATE TABLE `detalles_venta` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `venta_id` BIGINT NOT NULL,
  `producto_id` BIGINT NOT NULL,
  `cantidad` INT NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_venta` (`venta_id`),
  KEY `fk_detalle_producto` (`producto_id`),
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
  CONSTRAINT `fk_detalle_venta` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 8. TABLA: movimientos_inventario
-- -------------------------------------------------------------
CREATE TABLE `movimientos_inventario` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `fecha_hora` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `producto_id` BIGINT NOT NULL,
  `tipo_movimiento` ENUM('ENTRADA', 'SALIDA') NOT NULL,
  `motivo` ENUM('COMPRA', 'VENTA', 'MERMA_DANO', 'AJUSTE', 'DEVOLUCION') NOT NULL,
  `cantidad` INT NOT NULL,
  `stock_anterior` INT NOT NULL,
  `stock_nuevo` INT NOT NULL,
  `usuario` VARCHAR(100) NOT NULL,
  `observacion` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_movimiento_producto` (`producto_id`),
  CONSTRAINT `fk_movimiento_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- INSERCIÓN DE DATOS INICIALES (SEMILLERO)
-- ==============================================================

-- Usuarios del sistema (contraseñas listas para acceso)
INSERT INTO `usuarios` (`username`, `password`, `nombre_completo`, `email`, `rol`, `estado`) VALUES
('admin', 'admin123', 'Edwar Montoya', 'edwar.montoya@ewuarsoft.com', 'ADMINISTRADOR', 'ACTIVO'),
('cajero', 'cajero123', 'Carlos Andrés Vendedor', 'carlos.cajero@ewuarsoft.com', 'CAJERO', 'ACTIVO'),
('almacen', 'almacen123', 'Andrés Felipe Bodeguero', 'andres.almacen@ewuarsoft.com', 'ALMACENISTA', 'ACTIVO');

-- Categorías de inventario
INSERT INTO `categorias` (`nombre`, `descripcion`) VALUES
('Licores Nacionales', 'Aguardientes, rones y licores de fabricación nacional'),
('Licores Importados', 'Whiskies, tequilas, ginebras, vodkas y rones internacionales'),
('Cervezas', 'Cervezas artesanales, nacionales e importadas'),
('Vinos y Champagnes', 'Vinos tintos, blancos, rosados y vinos espumosos'),
('Bebidas y Pasabocas', 'Mezcladores, tónicas, gaseosas, energizantes y snacks');

-- Proveedores
INSERT INTO `proveedores` (`nit`, `razon_social`, `contacto`, `telefono`, `email`, `direccion`, `categoria_suministro`) VALUES
('900123456-1', 'Distribuidora Mayorista Antioquia S.A.S', 'Rodrigo Mendoza', '3105551234', 'ventas@distriantioquia.com', 'Calle 10 # 43E-12, Medellín', 'Licores Nacionales'),
('900987654-2', 'Importaciones y Bebidas Premium Ltda', 'Laura Zapata', '3157778899', 'contacto@bebidaspremium.co', 'Carrera 43A # 1-50, Medellín', 'Licores Importados'),
('901456123-3', 'Cervecería & Snacks de Colombia', 'Mauricio Echeverri', '3008889900', 'pedidos@cervezascol.com', 'Zona Industrial Belén, Medellín', 'Cervezas y Pasabocas');

-- Clientes
INSERT INTO `clientes` (`documento`, `nombre`, `telefono`, `email`, `direccion`) VALUES
('222222222', 'Cliente Mostrador (Venta Rápida)', '3001234567', 'mostrador@ewuarsoft.com', 'Punto de Venta Central'),
('1020456789', 'Carlos Andrés Gómez', '3119876543', 'carlos.gomez@gmail.com', 'Calle 45 # 12-34'),
('1037894561', 'Mariana Restrepo Duque', '3145678901', 'mrestrepo@hotmail.com', 'Carrera 80 # 32-15'),
('71234987', 'Felipe Morales Henao', '3206549871', 'felipe.morales@yahoo.es', 'Circular 4 # 73-20'),
('1017234567', 'Valentina Osorio Ríos', '3138899776', 'valentina.osorio@gmail.com', 'Calle 33 # 65C-10');

-- Productos con niveles de stock (algunos con stock bajo para activar alertas del dashboard)
INSERT INTO `productos` (`codigo`, `nombre`, `categoria_id`, `precio_compra`, `precio_venta`, `stock_actual`, `stock_minimo`, `unidad_medida`) VALUES
('LIC-001', 'Aguardiente Antioqueño Azul 750ml (Tapa Azul)', 1, 36000.00, 48000.00, 32, 10, 'Botella 750ml'),
('LIC-002', 'Aguardiente Antioqueño Rojo 750ml (Tradicional)', 1, 34000.00, 45000.00, 18, 10, 'Botella 750ml'),
('LIC-003', 'Ron Medellín Añejo 3 Años 750ml', 1, 38000.00, 52000.00, 15, 8, 'Botella 750ml'),
('LIC-004', 'Ron Medellín Extra Añejo 8 Años 750ml', 1, 55000.00, 75000.00, 4, 8, 'Botella 750ml'),
('LIC-005', 'Whisky Buchanan\'s De Luxe 12 Años 750ml', 2, 130000.00, 175000.00, 12, 5, 'Botella 750ml'),
('LIC-006', 'Whisky Old Parr 12 Años 750ml', 2, 135000.00, 180000.00, 3, 6, 'Botella 750ml'),
('LIC-007', 'Tequila Don Julio Blanco 700ml', 2, 180000.00, 240000.00, 7, 4, 'Botella 700ml'),
('CER-001', 'Cerveza Club Colombia Dorada 330ml', 3, 3200.00, 5500.00, 60, 24, 'Botella 330ml'),
('CER-002', 'Cerveza Corona Extra 355ml', 3, 4500.00, 8000.00, 48, 20, 'Botella 355ml'),
('CER-003', 'Cerveza BBC Cajicá Miel Artesanal 330ml', 3, 5800.00, 9500.00, 5, 12, 'Botella 330ml'),
('VIN-001', 'Vino Gato Negro Cabernet Sauvignon 750ml', 4, 28000.00, 42000.00, 14, 6, 'Botella 750ml'),
('VIN-002', 'Vino Casillero del Diablo Merlot 750ml', 4, 42000.00, 60000.00, 2, 6, 'Botella 750ml'),
('BEB-001', 'Agua Tónica Canada Dry 300ml', 5, 2200.00, 4000.00, 36, 12, 'Botella 300ml'),
('BEB-002', 'Bebida Energizante Red Bull 250ml', 5, 6500.00, 10000.00, 25, 10, 'Lata 250ml'),
('BEB-003', 'Papas Pringles Original 124g', 5, 8000.00, 13000.00, 16, 8, 'Tubo 124g');
