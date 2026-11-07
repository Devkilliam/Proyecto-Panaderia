-- PANADERÍA AURORA - SEGUNDO AVANCE - 25 PROCEDIMIENTOS
-- Trabajamos con Oracle Database 21c XE y nuestro esquema PANADERIA_AURORA en XEPDB1.
-- Utilizamos las 14 tablas que creamos en Panaderia_Aurora_01_Crear_Tablas.sql.
-- En esta etapa desarrollamos operaciones para crear, consultar, actualizar
-- y eliminar categorías, productos, ingredientes, proveedores, clientes y empleados.
-- También agregamos el procedimiento que utilizamos para registrar pedidos.
-- Desde Java llamaremos a estos procedimientos mediante CallableStatement.
-- Al ejecutar este archivo creamos procedimientos; no borramos tablas ni agregamos datos.
-- Dejamos las confirmaciones de las operaciones de escritura a cargo de Java
-- y tenemos presente que una clave foránea puede impedir borrar registros relacionados.
-- Devolvemos los resultados de consulta con SYS_REFCURSOR; además desarrollamos
-- los 15 cursores del otro archivo, tal como se solicita en la consigna.
-- Ejecutamos con F5 y revisamos que los 25 procedimientos estén válidos y sin errores.

SET SERVEROUTPUT ON
PROMPT === VERIFICAR ESQUEMA Y CONTENEDOR ANTES DE EJECUTAR ===
SELECT USER AS USUARIO, SYS_CONTEXT('USERENV', 'CON_NAME') AS CONTENEDOR FROM DUAL;



PROMPT ===== PROCEDIMIENTOS CRUD: CATEGORIA =====

CREATE OR REPLACE PROCEDURE PR_CATEGORIA_CREAR (
    p_nombre IN CATEGORIA.nombre%TYPE,
    p_descripcion IN CATEGORIA.descripcion%TYPE DEFAULT NULL,
    p_id_categoria OUT CATEGORIA.id_categoria%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'CATEGORIA.nombre es obligatorio');
    END IF;
    INSERT INTO CATEGORIA (nombre, descripcion)
    VALUES (p_nombre, p_descripcion)
    RETURNING id_categoria INTO p_id_categoria;
END;
/

CREATE OR REPLACE PROCEDURE PR_CATEGORIA_CONSULTAR (
    p_id_categoria IN CATEGORIA.id_categoria%TYPE DEFAULT NULL,
    p_resultado OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_resultado FOR
        SELECT id_categoria, nombre, descripcion
          FROM CATEGORIA
         WHERE (p_id_categoria IS NULL OR id_categoria = p_id_categoria)
         ORDER BY id_categoria;
END;
/

CREATE OR REPLACE PROCEDURE PR_CATEGORIA_ACTUALIZAR (
    p_id_categoria IN CATEGORIA.id_categoria%TYPE,
    p_nombre IN CATEGORIA.nombre%TYPE,
    p_descripcion IN CATEGORIA.descripcion%TYPE DEFAULT NULL
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'CATEGORIA.nombre es obligatorio');
    END IF;
    UPDATE CATEGORIA
       SET nombre = p_nombre,
            descripcion = p_descripcion
     WHERE id_categoria = p_id_categoria;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'CATEGORIA: no existe el identificador indicado');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_CATEGORIA_ELIMINAR (
    p_id_categoria IN CATEGORIA.id_categoria%TYPE
) AS
BEGIN
    DELETE FROM CATEGORIA WHERE id_categoria = p_id_categoria;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'CATEGORIA: no existe el identificador indicado');
    END IF;
END;
/


PROMPT ===== PROCEDIMIENTOS CRUD: PRODUCTO =====

CREATE OR REPLACE PROCEDURE PR_PRODUCTO_CREAR (
    p_id_categoria IN PRODUCTO.id_categoria%TYPE,
    p_nombre IN PRODUCTO.nombre%TYPE,
    p_precio_venta IN PRODUCTO.precio_venta%TYPE,
    p_activo IN PRODUCTO.activo%TYPE,
    p_id_producto OUT PRODUCTO.id_producto%TYPE
) AS
BEGIN
    IF p_id_categoria IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'PRODUCTO.id_categoria es obligatorio');
    END IF;
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'PRODUCTO.nombre es obligatorio');
    END IF;
    IF p_precio_venta IS NULL OR p_precio_venta < 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'PRODUCTO.precio_venta debe ser mayor o igual a cero');
    END IF;
    IF p_activo IS NULL OR p_activo NOT IN (0, 1) THEN
        RAISE_APPLICATION_ERROR(-20012, 'PRODUCTO.activo solo admite 0 o 1');
    END IF;
    INSERT INTO PRODUCTO (id_categoria, nombre, precio_venta, activo)
    VALUES (p_id_categoria, p_nombre, p_precio_venta, p_activo)
    RETURNING id_producto INTO p_id_producto;
END;
/

CREATE OR REPLACE PROCEDURE PR_PRODUCTO_CONSULTAR (
    p_id_producto IN PRODUCTO.id_producto%TYPE DEFAULT NULL,
    p_resultado OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_resultado FOR
        SELECT id_producto, id_categoria, nombre, precio_venta, activo
          FROM PRODUCTO
         WHERE (p_id_producto IS NULL OR id_producto = p_id_producto)
         ORDER BY id_producto;
END;
/

CREATE OR REPLACE PROCEDURE PR_PRODUCTO_ACTUALIZAR (
    p_id_producto IN PRODUCTO.id_producto%TYPE,
    p_id_categoria IN PRODUCTO.id_categoria%TYPE,
    p_nombre IN PRODUCTO.nombre%TYPE,
    p_precio_venta IN PRODUCTO.precio_venta%TYPE,
    p_activo IN PRODUCTO.activo%TYPE
) AS
BEGIN
    IF p_id_categoria IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'PRODUCTO.id_categoria es obligatorio');
    END IF;
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'PRODUCTO.nombre es obligatorio');
    END IF;
    IF p_precio_venta IS NULL OR p_precio_venta < 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'PRODUCTO.precio_venta debe ser mayor o igual a cero');
    END IF;
    IF p_activo IS NULL OR p_activo NOT IN (0, 1) THEN
        RAISE_APPLICATION_ERROR(-20012, 'PRODUCTO.activo solo admite 0 o 1');
    END IF;
    UPDATE PRODUCTO
       SET id_categoria = p_id_categoria,
            nombre = p_nombre,
            precio_venta = p_precio_venta,
            activo = p_activo
     WHERE id_producto = p_id_producto;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'PRODUCTO: no existe el identificador indicado');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_PRODUCTO_ELIMINAR (
    p_id_producto IN PRODUCTO.id_producto%TYPE
) AS
BEGIN
    DELETE FROM PRODUCTO WHERE id_producto = p_id_producto;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'PRODUCTO: no existe el identificador indicado');
    END IF;
END;
/


PROMPT ===== PROCEDIMIENTOS CRUD: INGREDIENTE =====

CREATE OR REPLACE PROCEDURE PR_INGREDIENTE_CREAR (
    p_nombre IN INGREDIENTE.nombre%TYPE,
    p_unidad_medida IN INGREDIENTE.unidad_medida%TYPE,
    p_stock_minimo IN INGREDIENTE.stock_minimo%TYPE,
    p_id_ingrediente OUT INGREDIENTE.id_ingrediente%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'INGREDIENTE.nombre es obligatorio');
    END IF;
    IF TRIM(p_unidad_medida) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'INGREDIENTE.unidad_medida es obligatorio');
    END IF;
    IF p_stock_minimo IS NULL OR p_stock_minimo < 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'INGREDIENTE.stock_minimo debe ser mayor o igual a cero');
    END IF;
    INSERT INTO INGREDIENTE (nombre, unidad_medida, stock_minimo)
    VALUES (p_nombre, p_unidad_medida, p_stock_minimo)
    RETURNING id_ingrediente INTO p_id_ingrediente;
END;
/

CREATE OR REPLACE PROCEDURE PR_INGREDIENTE_CONSULTAR (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE DEFAULT NULL,
    p_resultado OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_resultado FOR
        SELECT id_ingrediente, nombre, unidad_medida, stock_minimo
          FROM INGREDIENTE
         WHERE (p_id_ingrediente IS NULL OR id_ingrediente = p_id_ingrediente)
         ORDER BY id_ingrediente;
END;
/

CREATE OR REPLACE PROCEDURE PR_INGREDIENTE_ACTUALIZAR (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE,
    p_nombre IN INGREDIENTE.nombre%TYPE,
    p_unidad_medida IN INGREDIENTE.unidad_medida%TYPE,
    p_stock_minimo IN INGREDIENTE.stock_minimo%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'INGREDIENTE.nombre es obligatorio');
    END IF;
    IF TRIM(p_unidad_medida) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'INGREDIENTE.unidad_medida es obligatorio');
    END IF;
    IF p_stock_minimo IS NULL OR p_stock_minimo < 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'INGREDIENTE.stock_minimo debe ser mayor o igual a cero');
    END IF;
    UPDATE INGREDIENTE
       SET nombre = p_nombre,
            unidad_medida = p_unidad_medida,
            stock_minimo = p_stock_minimo
     WHERE id_ingrediente = p_id_ingrediente;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'INGREDIENTE: no existe el identificador indicado');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_INGREDIENTE_ELIMINAR (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) AS
BEGIN
    DELETE FROM INGREDIENTE WHERE id_ingrediente = p_id_ingrediente;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'INGREDIENTE: no existe el identificador indicado');
    END IF;
END;
/


PROMPT ===== PROCEDIMIENTOS CRUD: PROVEEDOR =====

CREATE OR REPLACE PROCEDURE PR_PROVEEDOR_CREAR (
    p_nombre IN PROVEEDOR.nombre%TYPE,
    p_telefono IN PROVEEDOR.telefono%TYPE DEFAULT NULL,
    p_correo IN PROVEEDOR.correo%TYPE DEFAULT NULL,
    p_id_proveedor OUT PROVEEDOR.id_proveedor%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'PROVEEDOR.nombre es obligatorio');
    END IF;
    INSERT INTO PROVEEDOR (nombre, telefono, correo)
    VALUES (p_nombre, p_telefono, p_correo)
    RETURNING id_proveedor INTO p_id_proveedor;
END;
/

CREATE OR REPLACE PROCEDURE PR_PROVEEDOR_CONSULTAR (
    p_id_proveedor IN PROVEEDOR.id_proveedor%TYPE DEFAULT NULL,
    p_resultado OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_resultado FOR
        SELECT id_proveedor, nombre, telefono, correo
          FROM PROVEEDOR
         WHERE (p_id_proveedor IS NULL OR id_proveedor = p_id_proveedor)
         ORDER BY id_proveedor;
END;
/

CREATE OR REPLACE PROCEDURE PR_PROVEEDOR_ACTUALIZAR (
    p_id_proveedor IN PROVEEDOR.id_proveedor%TYPE,
    p_nombre IN PROVEEDOR.nombre%TYPE,
    p_telefono IN PROVEEDOR.telefono%TYPE DEFAULT NULL,
    p_correo IN PROVEEDOR.correo%TYPE DEFAULT NULL
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'PROVEEDOR.nombre es obligatorio');
    END IF;
    UPDATE PROVEEDOR
       SET nombre = p_nombre,
            telefono = p_telefono,
            correo = p_correo
     WHERE id_proveedor = p_id_proveedor;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'PROVEEDOR: no existe el identificador indicado');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_PROVEEDOR_ELIMINAR (
    p_id_proveedor IN PROVEEDOR.id_proveedor%TYPE
) AS
BEGIN
    DELETE FROM PROVEEDOR WHERE id_proveedor = p_id_proveedor;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'PROVEEDOR: no existe el identificador indicado');
    END IF;
END;
/


PROMPT ===== PROCEDIMIENTOS CRUD: CLIENTE =====

CREATE OR REPLACE PROCEDURE PR_CLIENTE_CREAR (
    p_nombre IN CLIENTE.nombre%TYPE,
    p_telefono IN CLIENTE.telefono%TYPE DEFAULT NULL,
    p_correo IN CLIENTE.correo%TYPE DEFAULT NULL,
    p_id_cliente OUT CLIENTE.id_cliente%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'CLIENTE.nombre es obligatorio');
    END IF;
    INSERT INTO CLIENTE (nombre, telefono, correo)
    VALUES (p_nombre, p_telefono, p_correo)
    RETURNING id_cliente INTO p_id_cliente;
END;
/

CREATE OR REPLACE PROCEDURE PR_CLIENTE_CONSULTAR (
    p_id_cliente IN CLIENTE.id_cliente%TYPE DEFAULT NULL,
    p_resultado OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_resultado FOR
        SELECT id_cliente, nombre, telefono, correo
          FROM CLIENTE
         WHERE (p_id_cliente IS NULL OR id_cliente = p_id_cliente)
         ORDER BY id_cliente;
END;
/

CREATE OR REPLACE PROCEDURE PR_CLIENTE_ACTUALIZAR (
    p_id_cliente IN CLIENTE.id_cliente%TYPE,
    p_nombre IN CLIENTE.nombre%TYPE,
    p_telefono IN CLIENTE.telefono%TYPE DEFAULT NULL,
    p_correo IN CLIENTE.correo%TYPE DEFAULT NULL
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'CLIENTE.nombre es obligatorio');
    END IF;
    UPDATE CLIENTE
       SET nombre = p_nombre,
            telefono = p_telefono,
            correo = p_correo
     WHERE id_cliente = p_id_cliente;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'CLIENTE: no existe el identificador indicado');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_CLIENTE_ELIMINAR (
    p_id_cliente IN CLIENTE.id_cliente%TYPE
) AS
BEGIN
    DELETE FROM CLIENTE WHERE id_cliente = p_id_cliente;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'CLIENTE: no existe el identificador indicado');
    END IF;
END;
/


PROMPT ===== PROCEDIMIENTOS CRUD: EMPLEADO =====

CREATE OR REPLACE PROCEDURE PR_EMPLEADO_CREAR (
    p_nombre IN EMPLEADO.nombre%TYPE,
    p_rol IN EMPLEADO.rol%TYPE,
    p_id_empleado OUT EMPLEADO.id_empleado%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'EMPLEADO.nombre es obligatorio');
    END IF;
    IF TRIM(p_rol) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'EMPLEADO.rol es obligatorio');
    END IF;
    INSERT INTO EMPLEADO (nombre, rol)
    VALUES (p_nombre, p_rol)
    RETURNING id_empleado INTO p_id_empleado;
END;
/

CREATE OR REPLACE PROCEDURE PR_EMPLEADO_CONSULTAR (
    p_id_empleado IN EMPLEADO.id_empleado%TYPE DEFAULT NULL,
    p_resultado OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_resultado FOR
        SELECT id_empleado, nombre, rol
          FROM EMPLEADO
         WHERE (p_id_empleado IS NULL OR id_empleado = p_id_empleado)
         ORDER BY id_empleado;
END;
/

CREATE OR REPLACE PROCEDURE PR_EMPLEADO_ACTUALIZAR (
    p_id_empleado IN EMPLEADO.id_empleado%TYPE,
    p_nombre IN EMPLEADO.nombre%TYPE,
    p_rol IN EMPLEADO.rol%TYPE
) AS
BEGIN
    IF TRIM(p_nombre) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'EMPLEADO.nombre es obligatorio');
    END IF;
    IF TRIM(p_rol) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'EMPLEADO.rol es obligatorio');
    END IF;
    UPDATE EMPLEADO
       SET nombre = p_nombre,
            rol = p_rol
     WHERE id_empleado = p_id_empleado;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'EMPLEADO: no existe el identificador indicado');
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE PR_EMPLEADO_ELIMINAR (
    p_id_empleado IN EMPLEADO.id_empleado%TYPE
) AS
BEGIN
    DELETE FROM EMPLEADO WHERE id_empleado = p_id_empleado;
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'EMPLEADO: no existe el identificador indicado');
    END IF;
END;
/

PROMPT ===== PROCEDIMIENTO 25: CREAR PEDIDO =====
CREATE OR REPLACE PROCEDURE PR_PEDIDO_CREAR (
    p_id_cliente IN PEDIDO.id_cliente%TYPE,
    p_id_empleado IN PEDIDO.id_empleado%TYPE,
    p_fecha_entrega IN PEDIDO.fecha_entrega%TYPE DEFAULT NULL,
    p_tipo IN PEDIDO.tipo%TYPE DEFAULT 'MOSTRADOR',
    p_id_pedido OUT PEDIDO.id_pedido%TYPE
) AS
BEGIN
    IF p_id_cliente IS NULL OR p_id_empleado IS NULL THEN
        RAISE_APPLICATION_ERROR(-20030, 'PEDIDO: cliente y empleado son obligatorios');
    END IF;
    IF TRIM(p_tipo) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20031, 'PEDIDO: el tipo es obligatorio');
    END IF;
    INSERT INTO PEDIDO(id_cliente, id_empleado, fecha_entrega, tipo, estado)
    VALUES (p_id_cliente, p_id_empleado, p_fecha_entrega,
            UPPER(TRIM(p_tipo)), 'PENDIENTE')
    RETURNING id_pedido INTO p_id_pedido;
END;
/

PROMPT === VERIFICACION DE LOS 25 PROCEDIMIENTOS DE ESTE ARCHIVO ===
SELECT OBJECT_NAME, STATUS
  FROM USER_OBJECTS
 WHERE OBJECT_TYPE = 'PROCEDURE'
   AND (OBJECT_NAME LIKE 'PR_CATEGORIA_%'
        OR OBJECT_NAME LIKE 'PR_PRODUCTO_%'
        OR OBJECT_NAME LIKE 'PR_INGREDIENTE_%'
        OR OBJECT_NAME LIKE 'PR_PROVEEDOR_%'
        OR OBJECT_NAME LIKE 'PR_CLIENTE_%'
        OR OBJECT_NAME LIKE 'PR_EMPLEADO_%'
        OR OBJECT_NAME = 'PR_PEDIDO_CREAR')
 ORDER BY OBJECT_NAME;

PROMPT === TOTAL DE PROCEDIMIENTOS VALIDOS: ESPERADO 25 ===
SELECT COUNT(*) AS TOTAL_PROCEDIMIENTOS_VALIDOS
  FROM USER_OBJECTS
 WHERE OBJECT_TYPE = 'PROCEDURE'
   AND STATUS = 'VALID'
   AND (OBJECT_NAME LIKE 'PR_CATEGORIA_%'
        OR OBJECT_NAME LIKE 'PR_PRODUCTO_%'
        OR OBJECT_NAME LIKE 'PR_INGREDIENTE_%'
        OR OBJECT_NAME LIKE 'PR_PROVEEDOR_%'
        OR OBJECT_NAME LIKE 'PR_CLIENTE_%'
        OR OBJECT_NAME LIKE 'PR_EMPLEADO_%'
        OR OBJECT_NAME = 'PR_PEDIDO_CREAR');

PROMPT === ERRORES DE COMPILACION (SIN FILAS ES LO ESPERADO) ===
SELECT NAME, TYPE, LINE, POSITION, TEXT
  FROM USER_ERRORS
 WHERE TYPE = 'PROCEDURE'
   AND (NAME LIKE 'PR_CATEGORIA_%'
        OR NAME LIKE 'PR_PRODUCTO_%'
        OR NAME LIKE 'PR_INGREDIENTE_%'
        OR NAME LIKE 'PR_PROVEEDOR_%'
        OR NAME LIKE 'PR_CLIENTE_%'
        OR NAME LIKE 'PR_EMPLEADO_%'
        OR NAME = 'PR_PEDIDO_CREAR')
 ORDER BY NAME, SEQUENCE;
