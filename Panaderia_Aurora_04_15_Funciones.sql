-- ============================================================================
-- PANADERÍA AURORA - SEGUNDO AVANCE - 15 FUNCIONES PL/SQL
-- Utilizamos Oracle Database 21c XE y el esquema PANADERIA_AURORA en XEPDB1.
-- Tomamos como base las tablas del archivo Panaderia_Aurora_01_Crear_Tablas.sql.
-- Integrantes: Killiam Carmona Quesada y Yoselin Montero Tencio.
-- ============================================================================
-- Ejecutamos el archivo completo con F5 desde PANADERIA_AURORA, no desde SYSTEM.
-- Antes necesitamos tener creadas las 14 tablas y las 10 vistas.
-- Nuestras funciones consultan información sin modificar tablas ni confirmar cambios.
-- Usamos CREATE OR REPLACE para actualizar únicamente estas funciones.
-- Si aún no ingresamos datos, algunos resultados pueden ser cero.
-- Calculamos costos estimados, no precios reales de nuestras compras.
-- Cuando falta algún costo de una receta, devolvemos NULL
-- para no mostrar un resultado incompleto.
-- No incluimos contraseñas ni datos de acceso en este archivo.

SET SERVEROUTPUT ON

PROMPT ===== IDENTIDAD: REVISAR QUE SEA PANADERIA_AURORA / XEPDB1 =====
SELECT USER AS USUARIO,
       SYS_CONTEXT('USERENV', 'CON_NAME') AS CONTENEDOR
FROM DUAL;

-- 01. Calculamos cuánto tenemos de un ingrediente según su historial de movimientos.
CREATE OR REPLACE FUNCTION FN_EXISTENCIA_INGREDIENTE (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) RETURN NUMBER
IS
    v_existencia NUMBER;
BEGIN
    SELECT existencia INTO v_existencia
    FROM V_STOCK_INGREDIENTES
    WHERE id_ingrediente = p_id_ingrediente;

    RETURN v_existencia;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20101, 'El ingrediente no existe');
END;
/

-- 02. Mostramos 1 cuando llegamos al mínimo de inventario y 0 cuando estamos por encima.
CREATE OR REPLACE FUNCTION FN_INGREDIENTE_BAJO_MINIMO (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) RETURN NUMBER
IS
    v_existencia NUMBER;
    v_minimo NUMBER;
BEGIN
    SELECT existencia, stock_minimo
      INTO v_existencia, v_minimo
      FROM V_STOCK_INGREDIENTES
     WHERE id_ingrediente = p_id_ingrediente;

    IF v_existencia <= v_minimo THEN
        RETURN 1;
    END IF;
    RETURN 0;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20102, 'El ingrediente no existe');
END;
/

-- 03. Contamos los proveedores que tenemos asociados a cada ingrediente.
CREATE OR REPLACE FUNCTION FN_PROVEEDORES_INGREDIENTE (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM INGREDIENTE WHERE id_ingrediente = p_id_ingrediente;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20103, 'El ingrediente no existe');
    END IF;

    SELECT COUNT(*) INTO v_total
      FROM PROVEEDOR_INGREDIENTE
     WHERE id_ingrediente = p_id_ingrediente;
    RETURN v_total;
END;
/

-- 04. Buscamos el costo de referencia más bajo de un ingrediente, si existe.
CREATE OR REPLACE FUNCTION FN_COSTO_MIN_INGREDIENTE (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_costo NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM INGREDIENTE WHERE id_ingrediente = p_id_ingrediente;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20104, 'El ingrediente no existe');
    END IF;

    SELECT MIN(costo_referencial) INTO v_costo
      FROM PROVEEDOR_INGREDIENTE
     WHERE id_ingrediente = p_id_ingrediente;
    RETURN v_costo;
END;
/

-- 05. Estimamos cuánto cuestan los ingredientes necesarios para una receta.
-- Para hacer este cálculo necesitamos tener el costo de todos sus ingredientes.
-- Tomamos como referencia la unidad de medida que registramos para cada ingrediente.
CREATE OR REPLACE FUNCTION FN_COSTO_ESTIMADO_RECETA (
    p_id_receta IN RECETA.id_receta%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_ingredientes NUMBER;
    v_con_costo NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM RECETA WHERE id_receta = p_id_receta;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20105, 'La receta no existe');
    END IF;

    SELECT COUNT(*), COUNT(costos.costo_minimo),
           SUM(ri.cantidad_requerida * costos.costo_minimo)
      INTO v_ingredientes, v_con_costo, v_total
      FROM RECETA_INGREDIENTE ri
      LEFT JOIN (
          SELECT id_ingrediente, MIN(costo_referencial) AS costo_minimo
            FROM PROVEEDOR_INGREDIENTE
           GROUP BY id_ingrediente
      ) costos ON costos.id_ingrediente = ri.id_ingrediente
     WHERE ri.id_receta = p_id_receta;

    IF v_ingredientes = 0 OR v_con_costo <> v_ingredientes THEN
        RETURN NULL;
    END IF;
    RETURN ROUND(v_total, 2);
END;
/

-- 06. Calculamos el total de un pedido con las cantidades y precios guardados.
CREATE OR REPLACE FUNCTION FN_TOTAL_PEDIDO (
    p_id_pedido IN PEDIDO.id_pedido%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM PEDIDO WHERE id_pedido = p_id_pedido;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20106, 'El pedido no existe');
    END IF;

    SELECT NVL(SUM(cantidad * precio_unitario), 0) INTO v_total
      FROM DETALLE_PEDIDO
     WHERE id_pedido = p_id_pedido;
    RETURN v_total;
END;
/

-- 07. Sumamos las unidades solicitadas, no la cantidad de renglones del pedido.
CREATE OR REPLACE FUNCTION FN_UNIDADES_PEDIDO (
    p_id_pedido IN PEDIDO.id_pedido%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_unidades NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM PEDIDO WHERE id_pedido = p_id_pedido;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20107, 'El pedido no existe');
    END IF;

    SELECT NVL(SUM(cantidad), 0) INTO v_unidades
      FROM DETALLE_PEDIDO
     WHERE id_pedido = p_id_pedido;
    RETURN v_unidades;
END;
/

-- 08. Contamos los pedidos del cliente que aún están pendientes, en producción o listos.
CREATE OR REPLACE FUNCTION FN_PEDIDOS_ABIERTOS_CLIENTE (
    p_id_cliente IN CLIENTE.id_cliente%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM CLIENTE WHERE id_cliente = p_id_cliente;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20108, 'El cliente no existe');
    END IF;

    SELECT COUNT(*) INTO v_total
      FROM PEDIDO
     WHERE id_cliente = p_id_cliente
       AND estado IN ('PENDIENTE', 'EN_PRODUCCION', 'LISTO');
    RETURN v_total;
END;
/

-- 09. Sumamos las ventas registradas a nombre de un cliente.
CREATE OR REPLACE FUNCTION FN_VENTAS_CLIENTE (
    p_id_cliente IN CLIENTE.id_cliente%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM CLIENTE WHERE id_cliente = p_id_cliente;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20109, 'El cliente no existe');
    END IF;

    SELECT NVL(SUM(d.cantidad * d.precio_unitario), 0) INTO v_total
      FROM PEDIDO p
      JOIN VENTA v ON v.id_pedido = p.id_pedido
      LEFT JOIN DETALLE_PEDIDO d ON d.id_pedido = p.id_pedido
     WHERE p.id_cliente = p_id_cliente;
    RETURN v_total;
END;
/

-- 10. Consultamos el monto de una venta cuando tenemos su identificador.
CREATE OR REPLACE FUNCTION FN_TOTAL_VENTA (
    p_id_venta IN VENTA.id_venta%TYPE
) RETURN NUMBER
IS
    v_id_pedido VENTA.id_pedido%TYPE;
    v_total NUMBER;
BEGIN
    SELECT id_pedido INTO v_id_pedido
      FROM VENTA
     WHERE id_venta = p_id_venta;

    SELECT NVL(SUM(cantidad * precio_unitario), 0) INTO v_total
      FROM DETALLE_PEDIDO
     WHERE id_pedido = v_id_pedido;
    RETURN v_total;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20110, 'La venta no existe');
END;
/

-- 11. Sumamos las ventas entre las dos fechas indicadas, incluyendo ambos días.
CREATE OR REPLACE FUNCTION FN_VENTAS_PERIODO (
    p_desde IN DATE,
    p_hasta IN DATE
) RETURN NUMBER
IS
    v_total NUMBER;
BEGIN
    IF p_desde IS NULL OR p_hasta IS NULL
       OR TRUNC(p_hasta) < TRUNC(p_desde) THEN
        RAISE_APPLICATION_ERROR(-20111, 'Indica un rango valido de fechas');
    END IF;

    SELECT NVL(SUM(d.cantidad * d.precio_unitario), 0) INTO v_total
      FROM VENTA v
      LEFT JOIN DETALLE_PEDIDO d ON d.id_pedido = v.id_pedido
     WHERE v.fecha_venta >= TRUNC(p_desde)
       AND v.fecha_venta < TRUNC(p_hasta) + 1;
    RETURN v_total;
END;
/

-- 12. Contamos las unidades de un producto que aparecen en pedidos con venta.
CREATE OR REPLACE FUNCTION FN_UNIDADES_VENDIDAS_PRODUCTO (
    p_id_producto IN PRODUCTO.id_producto%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM PRODUCTO WHERE id_producto = p_id_producto;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20112, 'El producto no existe');
    END IF;

    SELECT NVL(SUM(d.cantidad), 0) INTO v_total
      FROM DETALLE_PEDIDO d
      JOIN VENTA v ON v.id_pedido = d.id_pedido
     WHERE d.id_producto = p_id_producto;
    RETURN v_total;
END;
/

-- 13. Calculamos el ingreso de cada producto usando los precios de sus pedidos.
CREATE OR REPLACE FUNCTION FN_INGRESOS_PRODUCTO (
    p_id_producto IN PRODUCTO.id_producto%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM PRODUCTO WHERE id_producto = p_id_producto;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20113, 'El producto no existe');
    END IF;

    SELECT NVL(SUM(d.cantidad * d.precio_unitario), 0) INTO v_total
      FROM DETALLE_PEDIDO d
      JOIN VENTA v ON v.id_pedido = d.id_pedido
     WHERE d.id_producto = p_id_producto;
    RETURN v_total;
END;
/

-- 14. Contamos los productos activos que tenemos en una categoría.
CREATE OR REPLACE FUNCTION FN_PRODUCTOS_ACTIVOS_CATEGORIA (
    p_id_categoria IN CATEGORIA.id_categoria%TYPE
) RETURN NUMBER
IS
    v_existe NUMBER;
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM CATEGORIA WHERE id_categoria = p_id_categoria;
    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20114, 'La categoria no existe');
    END IF;

    SELECT COUNT(*) INTO v_total
      FROM PRODUCTO
     WHERE id_categoria = p_id_categoria AND activo = 1;
    RETURN v_total;
END;
/

-- 15. Estimamos el rendimiento de la producción de acuerdo con la receta y los lotes.
CREATE OR REPLACE FUNCTION FN_RENDIMIENTO_PRODUCCION (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE
) RETURN NUMBER
IS
    v_rendimiento NUMBER;
BEGIN
    SELECT r.rendimiento * p.lotes_producidos INTO v_rendimiento
      FROM PRODUCCION p
      JOIN RECETA r ON r.id_receta = p.id_receta
     WHERE p.id_produccion = p_id_produccion;
    RETURN v_rendimiento;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20115, 'La produccion no existe');
END;
/

PROMPT ===== ESTADO DE LAS 15 FUNCIONES =====
SELECT object_name, status
FROM USER_OBJECTS
WHERE object_type = 'FUNCTION'
  AND object_name IN (
      'FN_EXISTENCIA_INGREDIENTE', 'FN_INGREDIENTE_BAJO_MINIMO',
      'FN_PROVEEDORES_INGREDIENTE', 'FN_COSTO_MIN_INGREDIENTE',
      'FN_COSTO_ESTIMADO_RECETA', 'FN_TOTAL_PEDIDO',
      'FN_UNIDADES_PEDIDO', 'FN_PEDIDOS_ABIERTOS_CLIENTE',
      'FN_VENTAS_CLIENTE', 'FN_TOTAL_VENTA', 'FN_VENTAS_PERIODO',
      'FN_UNIDADES_VENDIDAS_PRODUCTO', 'FN_INGRESOS_PRODUCTO',
      'FN_PRODUCTOS_ACTIVOS_CATEGORIA', 'FN_RENDIMIENTO_PRODUCCION'
  )
ORDER BY object_name;

PROMPT ===== TOTAL FUNCIONES VALIDAS (ESPERADO 15) =====
SELECT COUNT(*) AS TOTAL_FUNCIONES_VALIDAS
FROM USER_OBJECTS
WHERE object_type = 'FUNCTION'
  AND status = 'VALID'
  AND object_name IN (
      'FN_EXISTENCIA_INGREDIENTE', 'FN_INGREDIENTE_BAJO_MINIMO',
      'FN_PROVEEDORES_INGREDIENTE', 'FN_COSTO_MIN_INGREDIENTE',
      'FN_COSTO_ESTIMADO_RECETA', 'FN_TOTAL_PEDIDO',
      'FN_UNIDADES_PEDIDO', 'FN_PEDIDOS_ABIERTOS_CLIENTE',
      'FN_VENTAS_CLIENTE', 'FN_TOTAL_VENTA', 'FN_VENTAS_PERIODO',
      'FN_UNIDADES_VENDIDAS_PRODUCTO', 'FN_INGRESOS_PRODUCTO',
      'FN_PRODUCTOS_ACTIVOS_CATEGORIA', 'FN_RENDIMIENTO_PRODUCCION'
  );

PROMPT ===== ERRORES DE COMPILACION (ESPERADO: 0 FILAS) =====
SELECT name, type, line, position, text
FROM USER_ERRORS
WHERE type = 'FUNCTION'
  AND name IN (
      'FN_EXISTENCIA_INGREDIENTE', 'FN_INGREDIENTE_BAJO_MINIMO',
      'FN_PROVEEDORES_INGREDIENTE', 'FN_COSTO_MIN_INGREDIENTE',
      'FN_COSTO_ESTIMADO_RECETA', 'FN_TOTAL_PEDIDO',
      'FN_UNIDADES_PEDIDO', 'FN_PEDIDOS_ABIERTOS_CLIENTE',
      'FN_VENTAS_CLIENTE', 'FN_TOTAL_VENTA', 'FN_VENTAS_PERIODO',
      'FN_UNIDADES_VENDIDAS_PRODUCTO', 'FN_INGRESOS_PRODUCTO',
      'FN_PRODUCTOS_ACTIVOS_CATEGORIA', 'FN_RENDIMIENTO_PRODUCCION'
  )
ORDER BY name, sequence;

PROMPT ===== PRUEBA SEGURA SIN INSERTAR DATOS =====
SELECT FN_VENTAS_PERIODO(TRUNC(SYSDATE) - 30, TRUNC(SYSDATE))
       AS TOTAL_VENTAS_ULTIMOS_31_DIAS
FROM DUAL;
