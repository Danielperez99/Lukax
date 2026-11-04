-- ============================================================
-- LUKAX - Script de la base de datos (MySQL)
-- Ejecutar completo en MySQL Workbench. Es seguro repetirlo:
-- usa IF NOT EXISTS, pero las categorías solo se insertan si la tabla está vacía.
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_lukax
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_lukax;

CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    hash_contrasena VARCHAR(200) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- usuario_id NULL = categoría predefinida (la ven todos los usuarios)
CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NULL,
    nombre VARCHAR(50) NOT NULL,
    tipo ENUM('INGRESO','GASTO') NOT NULL,
    es_predefinida BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS transaccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    categoria_id INT NOT NULL,
    tipo ENUM('INGRESO','GASTO') NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    fecha DATE NOT NULL,
    descripcion VARCHAR(200),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT chk_monto CHECK (monto > 0)
);

CREATE TABLE IF NOT EXISTS presupuesto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    categoria_id INT NOT NULL,
    limite_mensual DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    UNIQUE (usuario_id, categoria_id),
    CONSTRAINT chk_limite CHECK (limite_mensual > 0)
);

INSERT INTO categoria (usuario_id, nombre, tipo, es_predefinida)
SELECT * FROM (
    SELECT NULL AS u, 'Alimentación' AS n, 'GASTO' AS t, TRUE AS p UNION ALL
    SELECT NULL, 'Transporte', 'GASTO', TRUE UNION ALL
    SELECT NULL, 'Material académico', 'GASTO', TRUE UNION ALL
    SELECT NULL, 'Entretenimiento', 'GASTO', TRUE UNION ALL
    SELECT NULL, 'Suscripciones', 'GASTO', TRUE UNION ALL
    SELECT NULL, 'Servicios', 'GASTO', TRUE UNION ALL
    SELECT NULL, 'Otros gastos', 'GASTO', TRUE UNION ALL
    SELECT NULL, 'Apoyo familiar', 'INGRESO', TRUE UNION ALL
    SELECT NULL, 'Beca o monitoria', 'INGRESO', TRUE UNION ALL
    SELECT NULL, 'Trabajo ocasional', 'INGRESO', TRUE UNION ALL
    SELECT NULL, 'Otros ingresos', 'INGRESO', TRUE
) AS nuevas
WHERE NOT EXISTS (SELECT 1 FROM categoria);

SELECT * FROM categoria;
