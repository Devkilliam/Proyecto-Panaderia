-- PANADERÍA AURORA - SEGUNDO AVANCE
-- Preparamos 10 vistas de consulta para nuestra base de datos Oracle 21c.
-- Ejecutamos este archivo desde PANADERIA_AURORA en XEPDB1.
-- Con estas vistas consultamos datos sin alterar los registros de las 14 tablas.
-- Usamos CREATE OR REPLACE VIEW para poder actualizar nuestras vistas.
-- Ejecutamos todo el archivo con F5 desde Oracle SQL Developer.

SET SERVEROUTPUT ON

PROMPT ===== IDENTIDAD DE LA CONEXION: DEBE SER PANADERIA_AURORA / XEPDB1 =====
SELECT USER AS USUARIO_ACTUAL,
       SYS_CONTEXT('USERENV', 'CON_NAME') AS CONTENEDOR
FROM DUAL;

PROMPT ===== 01. CATALOGO DE PRODUCTOS =====
CREATE OR REPLACE VIEW V_CATALOGO_PRODUCTOS AS
SELECT p.id_producto,
       p.nombre AS producto,
       c.id_categoria,
       c.nombre AS categoria,
       p.precio_venta,
       p.activo
FROM PRODUCTO p
JOIN CATEGORIA c ON c.id_categoria = p.id_categoria;

PROMPT ===== 02. EXISTENCIAS DE INGREDIENTES =====
-- Calculamos las existencias con la diferencia entre entradas y salidas registradas.
CREATE OR REPLACE VIEW V_STOCK_INGREDIENTES AS
SELECT i.id_ingrediente,
       i.nombre AS ingrediente,
       i.unidad_medida,
       i.stock_minimo,
       NVL(m.existencia, 0) AS existencia,
       CASE
         WHEN NVL(m.existencia, 0) <= 0 THEN 'SIN_EXISTENCIAS'
         WHEN NVL(m.existencia, 0) <= i.stock_minimo THEN 'BAJO_MINIMO'
         ELSE 'SUFICIENTE'
       END AS estado_stock
FROM INGREDIENTE i
LEFT JOIN (
    SELECT id_ingrediente,
           SUM(CASE
                 WHEN tipo IN ('ENTRADA', 'AJUSTE_ENTRADA') THEN cantidad
                 WHEN tipo IN ('SALIDA', 'AJUSTE_SALIDA') THEN -cantidad
                 ELSE 0
               END) AS existencia
    FROM MOVIMIENTO_INVENTARIO
    GROUP BY id_ingrediente
) m ON m.id_ingrediente = i.id_ingrediente;

PROMPT ===== 03. ALERTAS DE INVENTARIO =====
CREATE OR REPLACE VIEW V_ALERTAS_INVENTARIO AS
SELECT id_ingrediente,
       ingrediente,
       unidad_medida,
       stock_minimo,
       existencia,
       estado_stock
FROM V_STOCK_INGREDIENTES
WHERE existencia <= stock_minimo;

PROMPT ===== 04. PEDIDOS CON TOTAL CALCULADO =====
CREATE OR REPLACE VIEW V_PEDIDOS_RESUMEN AS
SELECT p.id_pedido,
       p.fecha_pedido,
       p.fecha_entrega,
       p.tipo,
       p.estado,
       c.id_cliente,
       c.nombre AS cliente,
       e.id_empleado,
       e.nombre AS empleado,
       NVL(d.lineas, 0) AS lineas,
       NVL(d.total_pedido, 0) AS total_pedido
FROM PEDIDO p
JOIN CLIENTE c ON c.id_cliente = p.id_cliente
JOIN EMPLEADO e ON e.id_empleado = p.id_empleado
LEFT JOIN (
    SELECT id_pedido,
           COUNT(*) AS lineas,
           SUM(cantidad * precio_unitario) AS total_pedido
    FROM DETALLE_PEDIDO
    GROUP BY id_pedido
) d ON d.id_pedido = p.id_pedido;

PROMPT ===== 05. DETALLE DE PEDIDOS =====
CREATE OR REPLACE VIEW V_DETALLE_PEDIDOS AS
SELECT d.id_detalle,
       d.id_pedido,
       p.fecha_pedido,
       c.nombre AS cliente,
       d.id_producto,
       pr.nombre AS producto,
       d.cantidad,
       d.precio_unitario,
       d.cantidad * d.precio_unitario AS subtotal
FROM DETALLE_PEDIDO d
JOIN PEDIDO p ON p.id_pedido = d.id_pedido
JOIN CLIENTE c ON c.id_cliente = p.id_cliente
JOIN PRODUCTO pr ON pr.id_producto = d.id_producto;

PROMPT ===== 06. VENTAS CON SU IMPORTE =====
CREATE OR REPLACE VIEW V_VENTAS_RESUMEN AS
SELECT v.id_venta,
       v.id_pedido,
       v.fecha_venta,
       v.medio_pago,
       pr.id_cliente,
       pr.cliente,
       pr.total_pedido AS total_venta
FROM VENTA v
JOIN V_PEDIDOS_RESUMEN pr ON pr.id_pedido = v.id_pedido;

PROMPT ===== 07. PRODUCTOS VENDIDOS: SOLO PEDIDOS CON VENTA REGISTRADA =====
CREATE OR REPLACE VIEW V_PRODUCTOS_MAS_VENDIDOS AS
SELECT pr.id_producto,
       pr.nombre AS producto,
       c.nombre AS categoria,
       COUNT(DISTINCT v.id_venta) AS ventas_registradas,
       SUM(d.cantidad) AS unidades_vendidas,
       SUM(d.cantidad * d.precio_unitario) AS ingreso_total
FROM VENTA v
JOIN DETALLE_PEDIDO d ON d.id_pedido = v.id_pedido
JOIN PRODUCTO pr ON pr.id_producto = d.id_producto
JOIN CATEGORIA c ON c.id_categoria = pr.id_categoria
GROUP BY pr.id_producto, pr.nombre, c.nombre;

PROMPT ===== 08. INGREDIENTES DE CADA RECETA =====
CREATE OR REPLACE VIEW V_RECETAS_DETALLE AS
SELECT r.id_receta,
       r.id_producto,
       p.nombre AS producto,
       r.version_receta,
       r.rendimiento,
       ri.id_ingrediente,
       i.nombre AS ingrediente,
       i.unidad_medida,
       ri.cantidad_requerida
FROM RECETA r
JOIN PRODUCTO p ON p.id_producto = r.id_producto
JOIN RECETA_INGREDIENTE ri ON ri.id_receta = r.id_receta
JOIN INGREDIENTE i ON i.id_ingrediente = ri.id_ingrediente;

PROMPT ===== 09. LOTES Y PRODUCCION =====
CREATE OR REPLACE VIEW V_PRODUCCION_RESUMEN AS
SELECT pr.id_produccion,
       pr.fecha_produccion,
       pr.estado,
       pr.id_receta,
       r.version_receta,
       p.id_producto,
       p.nombre AS producto,
       pr.id_empleado,
       e.nombre AS empleado_responsable,
       pr.lotes_producidos,
       r.rendimiento,
       pr.lotes_producidos * r.rendimiento AS rendimiento_estimado
FROM PRODUCCION pr
JOIN RECETA r ON r.id_receta = pr.id_receta
JOIN PRODUCTO p ON p.id_producto = r.id_producto
JOIN EMPLEADO e ON e.id_empleado = pr.id_empleado;

PROMPT ===== 10. PROVEEDORES E INGREDIENTES =====
CREATE OR REPLACE VIEW V_PROVEEDORES_INGREDIENTES AS
SELECT pr.id_proveedor,
       pr.nombre AS proveedor,
       pr.telefono,
       pr.correo,
       i.id_ingrediente,
       i.nombre AS ingrediente,
       i.unidad_medida,
       pi.costo_referencial
FROM PROVEEDOR_INGREDIENTE pi
JOIN PROVEEDOR pr ON pr.id_proveedor = pi.id_proveedor
JOIN INGREDIENTE i ON i.id_ingrediente = pi.id_ingrediente;

PROMPT ===== COMPROBACION DE LAS VISTAS =====
SELECT view_name
FROM USER_VIEWS
WHERE view_name IN (
 'V_CATALOGO_PRODUCTOS', 'V_STOCK_INGREDIENTES',
 'V_ALERTAS_INVENTARIO', 'V_PEDIDOS_RESUMEN',
 'V_DETALLE_PEDIDOS', 'V_VENTAS_RESUMEN',
 'V_PRODUCTOS_MAS_VENDIDOS', 'V_RECETAS_DETALLE',
 'V_PRODUCCION_RESUMEN', 'V_PROVEEDORES_INGREDIENTES'
)
ORDER BY view_name;

PROMPT ===== TOTAL DE VISTAS CREADAS =====
SELECT COUNT(*) AS TOTAL_VISTAS
FROM USER_VIEWS
WHERE view_name IN (
 'V_CATALOGO_PRODUCTOS', 'V_STOCK_INGREDIENTES',
 'V_ALERTAS_INVENTARIO', 'V_PEDIDOS_RESUMEN',
 'V_DETALLE_PEDIDOS', 'V_VENTAS_RESUMEN',
 'V_PRODUCTOS_MAS_VENDIDOS', 'V_RECETAS_DETALLE',
 'V_PRODUCCION_RESUMEN', 'V_PROVEEDORES_INGREDIENTES'
);

PROMPT ===== ESTADO DE COMPILACION =====
SELECT object_name, status
FROM USER_OBJECTS
WHERE object_type = 'VIEW'
  AND object_name IN (
 'V_CATALOGO_PRODUCTOS', 'V_STOCK_INGREDIENTES',
 'V_ALERTAS_INVENTARIO', 'V_PEDIDOS_RESUMEN',
 'V_DETALLE_PEDIDOS', 'V_VENTAS_RESUMEN',
 'V_PRODUCTOS_MAS_VENDIDOS', 'V_RECETAS_DETALLE',
 'V_PRODUCCION_RESUMEN', 'V_PROVEEDORES_INGREDIENTES'
)
ORDER BY object_name;

PROMPT ===== ERRORES DE COMPILACION (SIN FILAS ES LO ESPERADO) =====
SELECT name, type, line, position, text
FROM USER_ERRORS
WHERE type = 'VIEW'
  AND name LIKE 'V\_%' ESCAPE '\'
ORDER BY name, sequence;
