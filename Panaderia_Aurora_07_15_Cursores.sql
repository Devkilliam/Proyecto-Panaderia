-- PANADERÍA AURORA - SEGUNDO AVANCE
-- En este archivo desarrollamos los 15 cursores del proyecto.
-- Trabajamos en Oracle Database 21c XE con PANADERIA_AURORA en XEPDB1.
-- Integrantes: Killiam Carmona Quesada y Yoselin Montero Tencio.
-- Así organizamos nuestras consultas:
-- Declaramos 15 cursores explícitos dentro de 15 procedimientos almacenados.
-- Recorremos 13 cursores con FOR ... IN cursor LOOP.
-- Para los otros 2 utilizamos OPEN, FETCH y CLOSE, incluso al manejar errores.
-- Usamos nuestros reportes solo para consultar tablas y vistas.
-- Podemos encontrar estos cursores dentro del código de sus procedimientos
-- al consultar USER_SOURCE; no aparecen como objetos independientes.
-- Podemos ejecutar de nuevo el archivo, porque cada rutina usa CREATE OR REPLACE.
-- Para instalarlos abrimos PANADERIA_AURORA y ejecutamos todo con F5.
-- Después necesitaremos probar los reportes con datos reales o de prueba.

SET SERVEROUTPUT ON SIZE UNLIMITED

PROMPT === VERIFICAR USUARIO Y CONTENEDOR ===
SELECT USER AS USUARIO_ACTUAL,
       SYS_CONTEXT('USERENV', 'CON_NAME') AS CONTENEDOR
FROM DUAL;

PROMPT ===== 01. CATALOGO DE PRODUCTOS =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_PRODUCTOS
AS
    CURSOR C_PRODUCTOS IS
        SELECT id_producto, producto, categoria, precio_venta, activo
           FROM V_CATALOGO_PRODUCTOS
          ORDER BY producto, id_producto;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('01. CATALOGO DE PRODUCTOS');
    FOR r IN C_PRODUCTOS LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || r.id_producto || ' | Producto=' || r.producto || ' | Categoria=' || r.categoria || ' | Precio=' || r.precio_venta || ' | Activo=' || CASE WHEN r.activo = 1 THEN 'SI' ELSE 'NO' END);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 02. INGREDIENTES =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_INGREDIENTES
AS
    CURSOR C_INGREDIENTES IS
        SELECT id_ingrediente, nombre, unidad_medida, stock_minimo
           FROM INGREDIENTE
          ORDER BY nombre, id_ingrediente;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('02. INGREDIENTES');
    FOR r IN C_INGREDIENTES LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || r.id_ingrediente || ' | Ingrediente=' || r.nombre || ' | Unidad=' || r.unidad_medida || ' | Minimo=' || r.stock_minimo);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 03. EXISTENCIAS DE INGREDIENTES =====
CREATE OR REPLACE PROCEDURE CUR_REVISAR_STOCK
AS
    CURSOR C_STOCK IS
        SELECT id_ingrediente, ingrediente, unidad_medida, existencia, estado_stock
           FROM V_STOCK_INGREDIENTES
          ORDER BY ingrediente, id_ingrediente;
    v_total PLS_INTEGER := 0;
    v_fila C_STOCK%ROWTYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('03. EXISTENCIAS DE INGREDIENTES');
    OPEN C_STOCK;
    LOOP
        FETCH C_STOCK INTO v_fila;
        EXIT WHEN C_STOCK%NOTFOUND;
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || v_fila.id_ingrediente || ' | Ingrediente=' || v_fila.ingrediente || ' | Existencia=' || v_fila.existencia || ' ' || v_fila.unidad_medida || ' | Estado=' || v_fila.estado_stock);
    END LOOP;
    CLOSE C_STOCK;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
EXCEPTION
    WHEN OTHERS THEN
        IF C_STOCK%ISOPEN THEN
            CLOSE C_STOCK;
        END IF;
        RAISE;
END;
/

PROMPT ===== 04. ALERTAS DE INVENTARIO =====
CREATE OR REPLACE PROCEDURE CUR_ALERTAS_INVENTARIO
AS
    CURSOR C_ALERTAS IS
        SELECT id_ingrediente, ingrediente, existencia, stock_minimo, estado_stock
           FROM V_ALERTAS_INVENTARIO
          ORDER BY ingrediente, id_ingrediente;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('04. ALERTAS DE INVENTARIO');
    FOR r IN C_ALERTAS LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || r.id_ingrediente || ' | Ingrediente=' || r.ingrediente || ' | Disponible=' || r.existencia || ' | Minimo=' || r.stock_minimo || ' | Estado=' || r.estado_stock);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 05. PROVEEDORES =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_PROVEEDORES
AS
    CURSOR C_PROVEEDORES IS
        SELECT id_proveedor, nombre, telefono, correo
           FROM PROVEEDOR
          ORDER BY nombre, id_proveedor;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('05. PROVEEDORES');
    FOR r IN C_PROVEEDORES LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || r.id_proveedor || ' | Proveedor=' || r.nombre || ' | Tel=' || NVL(r.telefono,'SIN DATO') || ' | Correo=' || NVL(r.correo,'SIN DATO'));
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 06. INGREDIENTES POR PROVEEDOR =====
CREATE OR REPLACE PROCEDURE CUR_INGREDIENTES_PROVEEDOR
AS
    CURSOR C_PROVEEDOR_ING IS
        SELECT id_proveedor, proveedor, ingrediente, costo_referencial
           FROM V_PROVEEDORES_INGREDIENTES
          ORDER BY proveedor, ingrediente;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('06. INGREDIENTES POR PROVEEDOR');
    FOR r IN C_PROVEEDOR_ING LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Proveedor=' || r.proveedor || ' (ID=' || r.id_proveedor || ')' || ' | Ingrediente=' || r.ingrediente || ' | Costo referencial=' || NVL(TO_CHAR(r.costo_referencial),'SIN DATO'));
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 07. RECETAS DE PRODUCTOS =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_RECETAS
AS
    CURSOR C_RECETAS IS
        SELECT r.id_receta, p.nombre AS producto, r.version_receta, r.rendimiento
           FROM RECETA r
           JOIN PRODUCTO p ON p.id_producto = r.id_producto
          ORDER BY p.nombre, r.version_receta;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('07. RECETAS DE PRODUCTOS');
    FOR r IN C_RECETAS LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Receta=' || r.id_receta || ' | Producto=' || r.producto || ' | Version=' || r.version_receta || ' | Rendimiento=' || r.rendimiento);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 08. INGREDIENTES POR RECETA =====
CREATE OR REPLACE PROCEDURE CUR_INGREDIENTES_RECETA
AS
    CURSOR C_RECETA_ING IS
        SELECT id_receta, producto, ingrediente, cantidad_requerida, unidad_medida
           FROM V_RECETAS_DETALLE
          ORDER BY id_receta, ingrediente;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('08. INGREDIENTES POR RECETA');
    FOR r IN C_RECETA_ING LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Receta=' || r.id_receta || ' | Producto=' || r.producto || ' | Ingrediente=' || r.ingrediente || ' | Requerido=' || r.cantidad_requerida || ' ' || r.unidad_medida);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 09. CLIENTES =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_CLIENTES
AS
    CURSOR C_CLIENTES IS
        SELECT id_cliente, nombre, telefono
           FROM CLIENTE
          ORDER BY nombre, id_cliente;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('09. CLIENTES');
    FOR r IN C_CLIENTES LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || r.id_cliente || ' | Cliente=' || r.nombre || ' | Tel=' || NVL(r.telefono,'SIN DATO'));
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 10. EMPLEADOS =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_EMPLEADOS
AS
    CURSOR C_EMPLEADOS IS
        SELECT id_empleado, nombre, rol
           FROM EMPLEADO
          ORDER BY nombre, id_empleado;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('10. EMPLEADOS');
    FOR r IN C_EMPLEADOS LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('ID=' || r.id_empleado || ' | Empleado=' || r.nombre || ' | Rol=' || r.rol);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 11. PEDIDOS Y TOTALES =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_PEDIDOS
AS
    CURSOR C_PEDIDOS IS
        SELECT id_pedido, cliente, estado, fecha_pedido, total_pedido
           FROM V_PEDIDOS_RESUMEN
          ORDER BY fecha_pedido DESC, id_pedido DESC;
    v_total PLS_INTEGER := 0;
    v_fila C_PEDIDOS%ROWTYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('11. PEDIDOS Y TOTALES');
    OPEN C_PEDIDOS;
    LOOP
        FETCH C_PEDIDOS INTO v_fila;
        EXIT WHEN C_PEDIDOS%NOTFOUND;
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Pedido=' || v_fila.id_pedido || ' | Cliente=' || v_fila.cliente || ' | Estado=' || v_fila.estado || ' | Fecha=' || TO_CHAR(v_fila.fecha_pedido,'YYYY-MM-DD HH24:MI') || ' | Total=' || v_fila.total_pedido);
    END LOOP;
    CLOSE C_PEDIDOS;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
EXCEPTION
    WHEN OTHERS THEN
        IF C_PEDIDOS%ISOPEN THEN
            CLOSE C_PEDIDOS;
        END IF;
        RAISE;
END;
/

PROMPT ===== 12. DETALLE DE PEDIDOS =====
CREATE OR REPLACE PROCEDURE CUR_DETALLE_PEDIDOS
AS
    CURSOR C_DETALLES IS
        SELECT id_pedido, producto, cantidad, precio_unitario, subtotal
           FROM V_DETALLE_PEDIDOS
          ORDER BY id_pedido, producto;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('12. DETALLE DE PEDIDOS');
    FOR r IN C_DETALLES LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Pedido=' || r.id_pedido || ' | Producto=' || r.producto || ' | Cantidad=' || r.cantidad || ' | Precio=' || r.precio_unitario || ' | Subtotal=' || r.subtotal);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 13. VENTAS Y MEDIOS DE PAGO =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_VENTAS
AS
    CURSOR C_VENTAS IS
        SELECT id_venta, cliente, fecha_venta, medio_pago, total_venta
           FROM V_VENTAS_RESUMEN
          ORDER BY fecha_venta DESC, id_venta DESC;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('13. VENTAS Y MEDIOS DE PAGO');
    FOR r IN C_VENTAS LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Venta=' || r.id_venta || ' | Cliente=' || r.cliente || ' | Fecha=' || TO_CHAR(r.fecha_venta,'YYYY-MM-DD HH24:MI') || ' | Pago=' || r.medio_pago || ' | Total=' || r.total_venta);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 14. PRODUCCION Y LOTES =====
CREATE OR REPLACE PROCEDURE CUR_LISTAR_PRODUCCION
AS
    CURSOR C_PRODUCCION IS
        SELECT id_produccion, producto, lotes_producidos, rendimiento_estimado, estado
           FROM V_PRODUCCION_RESUMEN
          ORDER BY id_produccion DESC;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('14. PRODUCCION Y LOTES');
    FOR r IN C_PRODUCCION LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Produccion=' || r.id_produccion || ' | Producto=' || r.producto || ' | Lotes=' || r.lotes_producidos || ' | Rendimiento estimado=' || r.rendimiento_estimado || ' | Estado=' || r.estado);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== 15. MOVIMIENTOS DE INVENTARIO =====
CREATE OR REPLACE PROCEDURE CUR_MOVIMIENTOS_INVENTARIO
AS
    CURSOR C_MOVIMIENTOS IS
        SELECT m.id_movimiento, i.nombre AS ingrediente,
                  m.tipo, m.cantidad, m.fecha_movimiento
           FROM MOVIMIENTO_INVENTARIO m
           JOIN INGREDIENTE i ON i.id_ingrediente = m.id_ingrediente
          ORDER BY m.fecha_movimiento DESC, m.id_movimiento DESC;
    v_total PLS_INTEGER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('15. MOVIMIENTOS DE INVENTARIO');
    FOR r IN C_MOVIMIENTOS LOOP
        v_total := v_total + 1;
        DBMS_OUTPUT.PUT_LINE('Movimiento=' || r.id_movimiento || ' | Ingrediente=' || r.ingrediente || ' | Tipo=' || r.tipo || ' | Cantidad=' || r.cantidad || ' | Fecha=' || TO_CHAR(r.fecha_movimiento,'YYYY-MM-DD HH24:MI'));
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Filas recorridas: ' || v_total);
END;
/

PROMPT ===== COMPROBACION: 15 PROCEDIMIENTOS CON CURSORES VALIDOS =====
SELECT COUNT(*) AS TOTAL_PROCEDIMIENTOS_CURSORES_VALIDOS
FROM USER_OBJECTS
WHERE OBJECT_TYPE = 'PROCEDURE'
  AND STATUS = 'VALID'
  AND OBJECT_NAME IN (
    'CUR_LISTAR_PRODUCTOS',
    'CUR_LISTAR_INGREDIENTES',
    'CUR_REVISAR_STOCK',
    'CUR_ALERTAS_INVENTARIO',
    'CUR_LISTAR_PROVEEDORES',
    'CUR_INGREDIENTES_PROVEEDOR',
    'CUR_LISTAR_RECETAS',
    'CUR_INGREDIENTES_RECETA',
    'CUR_LISTAR_CLIENTES',
    'CUR_LISTAR_EMPLEADOS',
    'CUR_LISTAR_PEDIDOS',
    'CUR_DETALLE_PEDIDOS',
    'CUR_LISTAR_VENTAS',
    'CUR_LISTAR_PRODUCCION',
    'CUR_MOVIMIENTOS_INVENTARIO'
  );

PROMPT ===== COMPROBACION: 15 DECLARACIONES EXPLICITAS CURSOR =====
-- Para comprobarlos contamos las declaraciones CURSOR en el código compilado.
SELECT COUNT(*) AS TOTAL_CURSORES_EXPLICITOS
FROM USER_SOURCE
WHERE TYPE = 'PROCEDURE'
  AND NAME IN (
    'CUR_LISTAR_PRODUCTOS',
    'CUR_LISTAR_INGREDIENTES',
    'CUR_REVISAR_STOCK',
    'CUR_ALERTAS_INVENTARIO',
    'CUR_LISTAR_PROVEEDORES',
    'CUR_INGREDIENTES_PROVEEDOR',
    'CUR_LISTAR_RECETAS',
    'CUR_INGREDIENTES_RECETA',
    'CUR_LISTAR_CLIENTES',
    'CUR_LISTAR_EMPLEADOS',
    'CUR_LISTAR_PEDIDOS',
    'CUR_DETALLE_PEDIDOS',
    'CUR_LISTAR_VENTAS',
    'CUR_LISTAR_PRODUCCION',
    'CUR_MOVIMIENTOS_INVENTARIO'
  )
  AND REGEXP_LIKE(LTRIM(TEXT), '^CURSOR[[:space:]]+C_[A-Z0-9_]+[[:space:]]+IS', 'i');

PROMPT ===== DETALLE DE COMPILACION: SE ESPERAN 15 VALID =====
SELECT OBJECT_NAME, STATUS
FROM USER_OBJECTS
WHERE OBJECT_TYPE = 'PROCEDURE'
  AND OBJECT_NAME IN (
    'CUR_LISTAR_PRODUCTOS',
    'CUR_LISTAR_INGREDIENTES',
    'CUR_REVISAR_STOCK',
    'CUR_ALERTAS_INVENTARIO',
    'CUR_LISTAR_PROVEEDORES',
    'CUR_INGREDIENTES_PROVEEDOR',
    'CUR_LISTAR_RECETAS',
    'CUR_INGREDIENTES_RECETA',
    'CUR_LISTAR_CLIENTES',
    'CUR_LISTAR_EMPLEADOS',
    'CUR_LISTAR_PEDIDOS',
    'CUR_DETALLE_PEDIDOS',
    'CUR_LISTAR_VENTAS',
    'CUR_LISTAR_PRODUCCION',
    'CUR_MOVIMIENTOS_INVENTARIO'
  )
ORDER BY OBJECT_NAME;

PROMPT ===== ERRORES DE LOS CURSORES (SIN FILAS = SIN ERRORES) =====
SELECT NAME, TYPE, LINE, POSITION, TEXT
FROM USER_ERRORS
WHERE TYPE = 'PROCEDURE'
  AND NAME IN (
    'CUR_LISTAR_PRODUCTOS',
    'CUR_LISTAR_INGREDIENTES',
    'CUR_REVISAR_STOCK',
    'CUR_ALERTAS_INVENTARIO',
    'CUR_LISTAR_PROVEEDORES',
    'CUR_INGREDIENTES_PROVEEDOR',
    'CUR_LISTAR_RECETAS',
    'CUR_INGREDIENTES_RECETA',
    'CUR_LISTAR_CLIENTES',
    'CUR_LISTAR_EMPLEADOS',
    'CUR_LISTAR_PEDIDOS',
    'CUR_DETALLE_PEDIDOS',
    'CUR_LISTAR_VENTAS',
    'CUR_LISTAR_PRODUCCION',
    'CUR_MOVIMIENTOS_INVENTARIO'
  )
ORDER BY NAME, SEQUENCE;

PROMPT ===== PRUEBA DE EJECUCION DE 3 REPORTES (SOLO LECTURA) =====
-- Si todavía no registramos datos, el reporte nos indicará cero filas recorridas.
BEGIN
    CUR_LISTAR_PRODUCTOS;
    CUR_REVISAR_STOCK;
    CUR_LISTAR_PEDIDOS;
END;
/
