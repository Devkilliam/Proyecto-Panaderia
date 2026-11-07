-- ============================================================================
-- PANADERÍA AURORA - SEGUNDO AVANCE - 10 PAQUETES PL/SQL
-- Utilizamos Oracle Database 21c XE y el esquema PANADERIA_AURORA en XEPDB1.
-- Tomamos como punto de partida las tablas, vistas y procedimientos
-- que programamos anteriormente, además de nuestras 15 funciones.
-- ============================================================================
-- Creamos 10 paquetes con sus respectivas especificaciones y cuerpos.
-- En ocho de ellos completamos las operaciones CRUD de las tablas
-- que no cubrimos con los 25 procedimientos independientes.
-- Con los otros dos consultamos información de ventas e inventario.
-- Distinguimos las rutinas internas de estos paquetes de los procedimientos
-- independientes que creamos en el archivo anterior.
-- No incluimos órdenes para borrar tablas ni datos de demostración.
-- Al ejecutar este archivo instalamos el código sin ejecutar las operaciones CRUD.
-- Trabajamos desde PANADERIA_AURORA en XEPDB1 y ejecutamos todo con F5.
-- Utilizamos CREATE OR REPLACE para actualizar solamente los paquetes indicados.
-- Al terminar comprobamos que tengamos 10 PACKAGE y 10 PACKAGE BODY válidos.

SET SERVEROUTPUT ON
PROMPT ===== USUARIO Y CONTENEDOR: REVISAR PANADERIA_AURORA / XEPDB1 =====
SELECT USER AS USUARIO, SYS_CONTEXT('USERENV','CON_NAME') AS CONTENEDOR FROM DUAL;


PROMPT ===== PKG_PROVEEDOR_ING =====
-- Gestionamos la relación proveedor-ingrediente, su clave compuesta y sus costos opcionales.
CREATE OR REPLACE PACKAGE PKG_PROVEEDOR_ING AS
    PROCEDURE crear (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_id_ingrediente IN PROVEEDOR_INGREDIENTE.id_ingrediente%TYPE,
    p_costo_referencial IN PROVEEDOR_INGREDIENTE.costo_referencial%TYPE
);
    PROCEDURE consultar (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_id_ingrediente IN PROVEEDOR_INGREDIENTE.id_ingrediente%TYPE,
    p_costo_referencial IN PROVEEDOR_INGREDIENTE.costo_referencial%TYPE
);
    PROCEDURE eliminar (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_id_ingrediente IN PROVEEDOR_INGREDIENTE.id_ingrediente%TYPE
);
END PKG_PROVEEDOR_ING;
/
CREATE OR REPLACE PACKAGE BODY PKG_PROVEEDOR_ING AS
    PROCEDURE crear (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_id_ingrediente IN PROVEEDOR_INGREDIENTE.id_ingrediente%TYPE,
    p_costo_referencial IN PROVEEDOR_INGREDIENTE.costo_referencial%TYPE
) IS
        BEGIN

            IF p_costo_referencial < 0 THEN
                RAISE_APPLICATION_ERROR(-20201, 'El costo no puede ser negativo');
            END IF;
            INSERT INTO PROVEEDOR_INGREDIENTE(id_proveedor,id_ingrediente,costo_referencial)
            VALUES (p_id_proveedor,p_id_ingrediente,p_costo_referencial);
    END crear;

    PROCEDURE consultar (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR SELECT id_proveedor,id_ingrediente,costo_referencial
              FROM PROVEEDOR_INGREDIENTE
             WHERE p_id_proveedor IS NULL OR id_proveedor = p_id_proveedor
             ORDER BY id_proveedor,id_ingrediente;
    END consultar;

    PROCEDURE actualizar (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_id_ingrediente IN PROVEEDOR_INGREDIENTE.id_ingrediente%TYPE,
    p_costo_referencial IN PROVEEDOR_INGREDIENTE.costo_referencial%TYPE
) IS
        BEGIN

            IF p_costo_referencial < 0 THEN
                RAISE_APPLICATION_ERROR(-20201, 'El costo no puede ser negativo');
            END IF;
            UPDATE PROVEEDOR_INGREDIENTE SET costo_referencial = p_costo_referencial
             WHERE id_proveedor = p_id_proveedor AND id_ingrediente = p_id_ingrediente;
            IF SQL%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20202,'Relacion no encontrada'); END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_proveedor IN PROVEEDOR_INGREDIENTE.id_proveedor%TYPE,
    p_id_ingrediente IN PROVEEDOR_INGREDIENTE.id_ingrediente%TYPE
) IS
        BEGIN

            DELETE FROM PROVEEDOR_INGREDIENTE
             WHERE id_proveedor = p_id_proveedor AND id_ingrediente = p_id_ingrediente;
            IF SQL%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20202,'Relacion no encontrada'); END IF;
    END eliminar;

END PKG_PROVEEDOR_ING;
/

PROMPT ===== PKG_RECETA =====
-- Administramos las recetas y respetamos la versión única de cada producto.
CREATE OR REPLACE PACKAGE PKG_RECETA AS
    PROCEDURE crear (
    p_id_producto IN RECETA.id_producto%TYPE,
    p_version_receta IN RECETA.version_receta%TYPE,
    p_rendimiento IN RECETA.rendimiento%TYPE,
    p_id_receta OUT RECETA.id_receta%TYPE
);
    PROCEDURE consultar (
    p_id_receta IN RECETA.id_receta%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_receta IN RECETA.id_receta%TYPE,
    p_id_producto IN RECETA.id_producto%TYPE,
    p_version_receta IN RECETA.version_receta%TYPE,
    p_rendimiento IN RECETA.rendimiento%TYPE
);
    PROCEDURE eliminar (
    p_id_receta IN RECETA.id_receta%TYPE
);
END PKG_RECETA;
/
CREATE OR REPLACE PACKAGE BODY PKG_RECETA AS
    PROCEDURE crear (
    p_id_producto IN RECETA.id_producto%TYPE,
    p_version_receta IN RECETA.version_receta%TYPE,
    p_rendimiento IN RECETA.rendimiento%TYPE,
    p_id_receta OUT RECETA.id_receta%TYPE
) IS
        BEGIN
            IF p_version_receta IS NULL OR p_version_receta <= 0 OR
               p_rendimiento IS NULL OR p_rendimiento <= 0 THEN
                RAISE_APPLICATION_ERROR(-20203, 'Version y rendimiento deben ser positivos');
            END IF;
            INSERT INTO RECETA (id_producto, version_receta, rendimiento)
                VALUES (p_id_producto, p_version_receta, p_rendimiento)
                RETURNING id_receta INTO p_id_receta;
    END crear;

    PROCEDURE consultar (
    p_id_receta IN RECETA.id_receta%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN
            OPEN p_resultado FOR
                SELECT id_receta, id_producto, version_receta, rendimiento
                  FROM RECETA
                 WHERE (p_id_receta IS NULL OR id_receta = p_id_receta)
                 ORDER BY id_receta;
    END consultar;

    PROCEDURE actualizar (
    p_id_receta IN RECETA.id_receta%TYPE,
    p_id_producto IN RECETA.id_producto%TYPE,
    p_version_receta IN RECETA.version_receta%TYPE,
    p_rendimiento IN RECETA.rendimiento%TYPE
) IS
        BEGIN
            IF p_version_receta IS NULL OR p_version_receta <= 0 OR
               p_rendimiento IS NULL OR p_rendimiento <= 0 THEN
                RAISE_APPLICATION_ERROR(-20203, 'Version y rendimiento deben ser positivos');
            END IF;
            UPDATE RECETA
               SET id_producto = p_id_producto,
                   version_receta = p_version_receta,
                   rendimiento = p_rendimiento
             WHERE id_receta = p_id_receta;
            IF SQL%ROWCOUNT = 0 THEN
                RAISE_APPLICATION_ERROR(-20240, 'RECETA: registro inexistente');
            END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_receta IN RECETA.id_receta%TYPE
) IS
        BEGIN
            DELETE FROM RECETA WHERE id_receta = p_id_receta;
            IF SQL%ROWCOUNT = 0 THEN
                RAISE_APPLICATION_ERROR(-20241, 'RECETA: registro inexistente');
            END IF;
    END eliminar;

END PKG_RECETA;
/

PROMPT ===== PKG_RECETA_ING =====
-- Registramos los ingredientes de cada receta sin aceptar cantidades nulas o negativas.
CREATE OR REPLACE PACKAGE PKG_RECETA_ING AS
    PROCEDURE crear (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_id_ingrediente IN RECETA_INGREDIENTE.id_ingrediente%TYPE,
    p_cantidad_requerida IN RECETA_INGREDIENTE.cantidad_requerida%TYPE
);
    PROCEDURE consultar (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_id_ingrediente IN RECETA_INGREDIENTE.id_ingrediente%TYPE,
    p_cantidad_requerida IN RECETA_INGREDIENTE.cantidad_requerida%TYPE
);
    PROCEDURE eliminar (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_id_ingrediente IN RECETA_INGREDIENTE.id_ingrediente%TYPE
);
END PKG_RECETA_ING;
/
CREATE OR REPLACE PACKAGE BODY PKG_RECETA_ING AS
    PROCEDURE crear (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_id_ingrediente IN RECETA_INGREDIENTE.id_ingrediente%TYPE,
    p_cantidad_requerida IN RECETA_INGREDIENTE.cantidad_requerida%TYPE
) IS
        BEGIN

            IF p_cantidad_requerida IS NULL OR p_cantidad_requerida <= 0 THEN
               RAISE_APPLICATION_ERROR(-20204,'La cantidad requerida debe ser positiva');
            END IF;
            INSERT INTO RECETA_INGREDIENTE(id_receta,id_ingrediente,cantidad_requerida)
            VALUES(p_id_receta,p_id_ingrediente,p_cantidad_requerida);
    END crear;

    PROCEDURE consultar (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR
              SELECT id_receta,id_ingrediente,cantidad_requerida
              FROM RECETA_INGREDIENTE
              WHERE p_id_receta IS NULL OR id_receta=p_id_receta
              ORDER BY id_receta,id_ingrediente;
    END consultar;

    PROCEDURE actualizar (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_id_ingrediente IN RECETA_INGREDIENTE.id_ingrediente%TYPE,
    p_cantidad_requerida IN RECETA_INGREDIENTE.cantidad_requerida%TYPE
) IS
        BEGIN

            IF p_cantidad_requerida IS NULL OR p_cantidad_requerida <= 0 THEN
               RAISE_APPLICATION_ERROR(-20204,'La cantidad requerida debe ser positiva');
            END IF;
            UPDATE RECETA_INGREDIENTE SET cantidad_requerida=p_cantidad_requerida
            WHERE id_receta=p_id_receta AND id_ingrediente=p_id_ingrediente;
            IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20205,'Ingrediente no asociado a la receta'); END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_receta IN RECETA_INGREDIENTE.id_receta%TYPE,
    p_id_ingrediente IN RECETA_INGREDIENTE.id_ingrediente%TYPE
) IS
        BEGIN

            DELETE FROM RECETA_INGREDIENTE
            WHERE id_receta=p_id_receta AND id_ingrediente=p_id_ingrediente;
            IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20205,'Ingrediente no asociado a la receta'); END IF;
    END eliminar;

END PKG_RECETA_ING;
/

PROMPT ===== PKG_PEDIDO =====
-- Gestionamos los pedidos y actualizamos su estado y su fecha de entrega.
CREATE OR REPLACE PACKAGE PKG_PEDIDO AS
    PROCEDURE crear (
    p_id_cliente IN PEDIDO.id_cliente%TYPE,
    p_id_empleado IN PEDIDO.id_empleado%TYPE,
    p_fecha_entrega IN PEDIDO.fecha_entrega%TYPE,
    p_tipo IN PEDIDO.tipo%TYPE,
    p_id_pedido OUT PEDIDO.id_pedido%TYPE
);
    PROCEDURE consultar (
    p_id_pedido IN PEDIDO.id_pedido%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_pedido IN PEDIDO.id_pedido%TYPE,
    p_fecha_entrega IN PEDIDO.fecha_entrega%TYPE,
    p_estado IN PEDIDO.estado%TYPE
);
    PROCEDURE eliminar (
    p_id_pedido IN PEDIDO.id_pedido%TYPE
);
    FUNCTION total (
    p_id_pedido IN PEDIDO.id_pedido%TYPE
) RETURN NUMBER;
END PKG_PEDIDO;
/
CREATE OR REPLACE PACKAGE BODY PKG_PEDIDO AS
    PROCEDURE crear (
    p_id_cliente IN PEDIDO.id_cliente%TYPE,
    p_id_empleado IN PEDIDO.id_empleado%TYPE,
    p_fecha_entrega IN PEDIDO.fecha_entrega%TYPE,
    p_tipo IN PEDIDO.tipo%TYPE,
    p_id_pedido OUT PEDIDO.id_pedido%TYPE
) IS
        BEGIN

            PR_PEDIDO_CREAR(p_id_cliente,p_id_empleado,p_fecha_entrega,p_tipo,p_id_pedido);
    END crear;

    PROCEDURE consultar (
    p_id_pedido IN PEDIDO.id_pedido%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN
            OPEN p_resultado FOR
                SELECT id_pedido, id_cliente, id_empleado, fecha_pedido, fecha_entrega, tipo, estado
                  FROM PEDIDO
                 WHERE (p_id_pedido IS NULL OR id_pedido = p_id_pedido)
                 ORDER BY id_pedido;
    END consultar;

    PROCEDURE actualizar (
    p_id_pedido IN PEDIDO.id_pedido%TYPE,
    p_fecha_entrega IN PEDIDO.fecha_entrega%TYPE,
    p_estado IN PEDIDO.estado%TYPE
) IS
        BEGIN

            IF p_estado IS NULL OR p_estado NOT IN
               ('PENDIENTE','EN_PRODUCCION','LISTO','ENTREGADO','CANCELADO') THEN
               RAISE_APPLICATION_ERROR(-20206,'Estado de pedido invalido');
            END IF;
            UPDATE PEDIDO SET fecha_entrega=p_fecha_entrega,estado=p_estado
             WHERE id_pedido=p_id_pedido;
            IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20207,'Pedido inexistente'); END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_pedido IN PEDIDO.id_pedido%TYPE
) IS
        BEGIN
            DELETE FROM PEDIDO WHERE id_pedido = p_id_pedido;
            IF SQL%ROWCOUNT = 0 THEN
                RAISE_APPLICATION_ERROR(-20241, 'PEDIDO: registro inexistente');
            END IF;
    END eliminar;

    FUNCTION total (
    p_id_pedido IN PEDIDO.id_pedido%TYPE
) RETURN NUMBER IS
        BEGIN

            RETURN FN_TOTAL_PEDIDO(p_id_pedido);
    END total;

END PKG_PEDIDO;
/

PROMPT ===== PKG_DETALLE_PEDIDO =====
-- Guardamos el precio del producto al crear una línea y permitimos cambiar su cantidad.
CREATE OR REPLACE PACKAGE PKG_DETALLE_PEDIDO AS
    PROCEDURE crear (
    p_id_pedido IN DETALLE_PEDIDO.id_pedido%TYPE,
    p_id_producto IN DETALLE_PEDIDO.id_producto%TYPE,
    p_cantidad IN DETALLE_PEDIDO.cantidad%TYPE,
    p_id_detalle OUT DETALLE_PEDIDO.id_detalle%TYPE
);
    PROCEDURE consultar (
    p_id_detalle IN DETALLE_PEDIDO.id_detalle%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_detalle IN DETALLE_PEDIDO.id_detalle%TYPE,
    p_cantidad IN DETALLE_PEDIDO.cantidad%TYPE
);
    PROCEDURE eliminar (
    p_id_detalle IN DETALLE_PEDIDO.id_detalle%TYPE
);
END PKG_DETALLE_PEDIDO;
/
CREATE OR REPLACE PACKAGE BODY PKG_DETALLE_PEDIDO AS
    PROCEDURE crear (
    p_id_pedido IN DETALLE_PEDIDO.id_pedido%TYPE,
    p_id_producto IN DETALLE_PEDIDO.id_producto%TYPE,
    p_cantidad IN DETALLE_PEDIDO.cantidad%TYPE,
    p_id_detalle OUT DETALLE_PEDIDO.id_detalle%TYPE
) IS
            v_precio PRODUCTO.precio_venta%TYPE;
            v_activo PRODUCTO.activo%TYPE;
        BEGIN

            IF p_cantidad IS NULL OR p_cantidad <= 0 OR p_cantidad <> TRUNC(p_cantidad) THEN
               RAISE_APPLICATION_ERROR(-20208,'Cantidad de unidades invalida');
            END IF;
            SELECT precio_venta,activo INTO v_precio,v_activo
              FROM PRODUCTO WHERE id_producto=p_id_producto;
            IF v_activo<>1 THEN
               RAISE_APPLICATION_ERROR(-20209,'No se puede vender un producto inactivo');
            END IF;
            INSERT INTO DETALLE_PEDIDO(id_pedido,id_producto,cantidad,precio_unitario)
            VALUES(p_id_pedido,p_id_producto,p_cantidad,v_precio)
            RETURNING id_detalle INTO p_id_detalle;
    END crear;

    PROCEDURE consultar (
    p_id_detalle IN DETALLE_PEDIDO.id_detalle%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN
            OPEN p_resultado FOR
                SELECT id_detalle, id_pedido, id_producto, cantidad, precio_unitario
                  FROM DETALLE_PEDIDO
                 WHERE (p_id_detalle IS NULL OR id_detalle = p_id_detalle)
                 ORDER BY id_detalle;
    END consultar;

    PROCEDURE actualizar (
    p_id_detalle IN DETALLE_PEDIDO.id_detalle%TYPE,
    p_cantidad IN DETALLE_PEDIDO.cantidad%TYPE
) IS
        BEGIN

            IF p_cantidad IS NULL OR p_cantidad<=0 OR p_cantidad<>TRUNC(p_cantidad) THEN
               RAISE_APPLICATION_ERROR(-20208,'Cantidad de unidades invalida');
            END IF;
            UPDATE DETALLE_PEDIDO SET cantidad=p_cantidad WHERE id_detalle=p_id_detalle;
            IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20210,'Detalle inexistente'); END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_detalle IN DETALLE_PEDIDO.id_detalle%TYPE
) IS
        BEGIN
            DELETE FROM DETALLE_PEDIDO WHERE id_detalle = p_id_detalle;
            IF SQL%ROWCOUNT = 0 THEN
                RAISE_APPLICATION_ERROR(-20241, 'DETALLE_PEDIDO: registro inexistente');
            END IF;
    END eliminar;

END PKG_DETALLE_PEDIDO;
/

PROMPT ===== PKG_PRODUCCION =====
-- Registramos los lotes; anotamos el consumo de ingredientes por separado en inventario.
CREATE OR REPLACE PACKAGE PKG_PRODUCCION AS
    PROCEDURE crear (
    p_id_receta IN PRODUCCION.id_receta%TYPE,
    p_id_empleado IN PRODUCCION.id_empleado%TYPE,
    p_lotes_producidos IN PRODUCCION.lotes_producidos%TYPE,
    p_id_produccion OUT PRODUCCION.id_produccion%TYPE
);
    PROCEDURE consultar (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE,
    p_lotes_producidos IN PRODUCCION.lotes_producidos%TYPE,
    p_estado IN PRODUCCION.estado%TYPE
);
    PROCEDURE eliminar (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE
);
END PKG_PRODUCCION;
/
CREATE OR REPLACE PACKAGE BODY PKG_PRODUCCION AS
    PROCEDURE crear (
    p_id_receta IN PRODUCCION.id_receta%TYPE,
    p_id_empleado IN PRODUCCION.id_empleado%TYPE,
    p_lotes_producidos IN PRODUCCION.lotes_producidos%TYPE,
    p_id_produccion OUT PRODUCCION.id_produccion%TYPE
) IS
        BEGIN

            IF p_lotes_producidos IS NULL OR p_lotes_producidos<=0
               OR p_lotes_producidos<>TRUNC(p_lotes_producidos) THEN
               RAISE_APPLICATION_ERROR(-20211,'Numero de lotes invalido');
            END IF;
            INSERT INTO PRODUCCION(id_receta,id_empleado,lotes_producidos)
            VALUES(p_id_receta,p_id_empleado,p_lotes_producidos)
            RETURNING id_produccion INTO p_id_produccion;
    END crear;

    PROCEDURE consultar (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN
            OPEN p_resultado FOR
                SELECT id_produccion, id_receta, id_empleado, fecha_produccion, lotes_producidos, estado
                  FROM PRODUCCION
                 WHERE (p_id_produccion IS NULL OR id_produccion = p_id_produccion)
                 ORDER BY id_produccion;
    END consultar;

    PROCEDURE actualizar (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE,
    p_lotes_producidos IN PRODUCCION.lotes_producidos%TYPE,
    p_estado IN PRODUCCION.estado%TYPE
) IS
        BEGIN

            IF p_lotes_producidos IS NULL OR p_lotes_producidos<=0
               OR p_lotes_producidos<>TRUNC(p_lotes_producidos) THEN
               RAISE_APPLICATION_ERROR(-20211,'Numero de lotes invalido');
            END IF;
            IF p_estado IS NULL OR p_estado NOT IN
               ('PLANIFICADA','EN_PROCESO','COMPLETADA','CANCELADA') THEN
               RAISE_APPLICATION_ERROR(-20212,'Estado de produccion invalido');
            END IF;
            UPDATE PRODUCCION SET lotes_producidos=p_lotes_producidos,estado=p_estado
            WHERE id_produccion=p_id_produccion;
            IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20213,'Produccion inexistente'); END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_produccion IN PRODUCCION.id_produccion%TYPE
) IS
        BEGIN
            DELETE FROM PRODUCCION WHERE id_produccion = p_id_produccion;
            IF SQL%ROWCOUNT = 0 THEN
                RAISE_APPLICATION_ERROR(-20241, 'PRODUCCION: registro inexistente');
            END IF;
    END eliminar;

END PKG_PRODUCCION;
/

PROMPT ===== PKG_VENTA =====
-- Registramos las ventas, sin incluir facturación electrónica ni procesamiento de pagos.
CREATE OR REPLACE PACKAGE PKG_VENTA AS
    PROCEDURE crear (
    p_id_pedido IN VENTA.id_pedido%TYPE,
    p_medio_pago IN VENTA.medio_pago%TYPE,
    p_id_venta OUT VENTA.id_venta%TYPE
);
    PROCEDURE consultar (
    p_id_venta IN VENTA.id_venta%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_venta IN VENTA.id_venta%TYPE,
    p_medio_pago IN VENTA.medio_pago%TYPE
);
    PROCEDURE eliminar (
    p_id_venta IN VENTA.id_venta%TYPE
);
    FUNCTION total (
    p_id_venta IN VENTA.id_venta%TYPE
) RETURN NUMBER;
END PKG_VENTA;
/
CREATE OR REPLACE PACKAGE BODY PKG_VENTA AS
    PROCEDURE crear (
    p_id_pedido IN VENTA.id_pedido%TYPE,
    p_medio_pago IN VENTA.medio_pago%TYPE,
    p_id_venta OUT VENTA.id_venta%TYPE
) IS
            v_lineas NUMBER;
        BEGIN

            IF TRIM(p_medio_pago) IS NULL THEN
               RAISE_APPLICATION_ERROR(-20214,'Medio de pago obligatorio');
            END IF;
            SELECT COUNT(*) INTO v_lineas FROM DETALLE_PEDIDO WHERE id_pedido=p_id_pedido;
            IF v_lineas=0 THEN
               RAISE_APPLICATION_ERROR(-20215,'El pedido debe contener productos para registrar venta');
            END IF;
            INSERT INTO VENTA(id_pedido,medio_pago) VALUES(p_id_pedido,TRIM(p_medio_pago))
              RETURNING id_venta INTO p_id_venta;
    END crear;

    PROCEDURE consultar (
    p_id_venta IN VENTA.id_venta%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN
            OPEN p_resultado FOR
                SELECT id_venta, id_pedido, fecha_venta, medio_pago
                  FROM VENTA
                 WHERE (p_id_venta IS NULL OR id_venta = p_id_venta)
                 ORDER BY id_venta;
    END consultar;

    PROCEDURE actualizar (
    p_id_venta IN VENTA.id_venta%TYPE,
    p_medio_pago IN VENTA.medio_pago%TYPE
) IS
        BEGIN

            IF TRIM(p_medio_pago) IS NULL THEN
                RAISE_APPLICATION_ERROR(-20214,'Medio de pago obligatorio');
            END IF;
            UPDATE VENTA SET medio_pago=TRIM(p_medio_pago) WHERE id_venta=p_id_venta;
            IF SQL%ROWCOUNT=0 THEN RAISE_APPLICATION_ERROR(-20216,'Venta inexistente'); END IF;
    END actualizar;

    PROCEDURE eliminar (
    p_id_venta IN VENTA.id_venta%TYPE
) IS
        BEGIN
            DELETE FROM VENTA WHERE id_venta = p_id_venta;
            IF SQL%ROWCOUNT = 0 THEN
                RAISE_APPLICATION_ERROR(-20241, 'VENTA: registro inexistente');
            END IF;
    END eliminar;

    FUNCTION total (
    p_id_venta IN VENTA.id_venta%TYPE
) RETURN NUMBER IS
        BEGIN

            RETURN FN_TOTAL_VENTA(p_id_venta);
    END total;

END PKG_VENTA;
/

PROMPT ===== PKG_MOVIMIENTO_INV =====
-- Administramos los movimientos del inventario y evitamos existencias negativas
-- mediante bloqueos por ingrediente cuando usamos las operaciones de este paquete.
-- Por eso procuramos hacer los movimientos desde este módulo y no mediante SQL directo.
CREATE OR REPLACE PACKAGE PKG_MOVIMIENTO_INV AS
    PROCEDURE crear (
    p_id_ingrediente IN MOVIMIENTO_INVENTARIO.id_ingrediente%TYPE,
    p_id_empleado IN MOVIMIENTO_INVENTARIO.id_empleado%TYPE,
    p_id_proveedor IN MOVIMIENTO_INVENTARIO.id_proveedor%TYPE,
    p_id_produccion IN MOVIMIENTO_INVENTARIO.id_produccion%TYPE,
    p_tipo IN MOVIMIENTO_INVENTARIO.tipo%TYPE,
    p_cantidad IN MOVIMIENTO_INVENTARIO.cantidad%TYPE,
    p_id_movimiento OUT MOVIMIENTO_INVENTARIO.id_movimiento%TYPE
);
    PROCEDURE consultar (
    p_id_movimiento IN MOVIMIENTO_INVENTARIO.id_movimiento%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE actualizar (
    p_id_movimiento IN MOVIMIENTO_INVENTARIO.id_movimiento%TYPE,
    p_tipo IN MOVIMIENTO_INVENTARIO.tipo%TYPE,
    p_cantidad IN MOVIMIENTO_INVENTARIO.cantidad%TYPE
);
    PROCEDURE eliminar (
    p_id_movimiento IN MOVIMIENTO_INVENTARIO.id_movimiento%TYPE
);
    FUNCTION existencia (
    p_id_ingrediente IN MOVIMIENTO_INVENTARIO.id_ingrediente%TYPE
) RETURN NUMBER;
END PKG_MOVIMIENTO_INV;
/
CREATE OR REPLACE PACKAGE BODY PKG_MOVIMIENTO_INV AS
    PROCEDURE crear (
    p_id_ingrediente IN MOVIMIENTO_INVENTARIO.id_ingrediente%TYPE,
    p_id_empleado IN MOVIMIENTO_INVENTARIO.id_empleado%TYPE,
    p_id_proveedor IN MOVIMIENTO_INVENTARIO.id_proveedor%TYPE,
    p_id_produccion IN MOVIMIENTO_INVENTARIO.id_produccion%TYPE,
    p_tipo IN MOVIMIENTO_INVENTARIO.tipo%TYPE,
    p_cantidad IN MOVIMIENTO_INVENTARIO.cantidad%TYPE,
    p_id_movimiento OUT MOVIMIENTO_INVENTARIO.id_movimiento%TYPE
) IS
            v_id INGREDIENTE.id_ingrediente%TYPE;
            v_tipo MOVIMIENTO_INVENTARIO.tipo%TYPE;
            v_disponible NUMBER;
        BEGIN

            IF p_cantidad IS NULL OR p_cantidad<=0 OR p_tipo IS NULL OR UPPER(TRIM(p_tipo)) NOT IN
               ('ENTRADA','SALIDA','AJUSTE_ENTRADA','AJUSTE_SALIDA') THEN
                RAISE_APPLICATION_ERROR(-20217,'Tipo o cantidad de movimiento invalido');
            END IF;
            -- Bloqueamos el ingrediente para evitar ajustes simultáneos desde este paquete.
            SELECT id_ingrediente INTO v_id FROM INGREDIENTE
             WHERE id_ingrediente=p_id_ingrediente FOR UPDATE;
            v_tipo := UPPER(TRIM(p_tipo));
            IF v_tipo IN ('SALIDA','AJUSTE_SALIDA') THEN
                v_disponible := FN_EXISTENCIA_INGREDIENTE(p_id_ingrediente);
                IF v_disponible<p_cantidad THEN
                    RAISE_APPLICATION_ERROR(-20218,'Inventario insuficiente para la salida');
                END IF;
            END IF;
            INSERT INTO MOVIMIENTO_INVENTARIO
               (id_ingrediente,id_empleado,id_proveedor,id_produccion,tipo,cantidad)
            VALUES (p_id_ingrediente,p_id_empleado,p_id_proveedor,p_id_produccion,v_tipo,p_cantidad)
            RETURNING id_movimiento INTO p_id_movimiento;
    END crear;

    PROCEDURE consultar (
    p_id_movimiento IN MOVIMIENTO_INVENTARIO.id_movimiento%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN
            OPEN p_resultado FOR
                SELECT id_movimiento, id_ingrediente, id_empleado, id_proveedor, id_produccion, tipo, cantidad, fecha_movimiento
                  FROM MOVIMIENTO_INVENTARIO
                 WHERE (p_id_movimiento IS NULL OR id_movimiento = p_id_movimiento)
                 ORDER BY id_movimiento;
    END consultar;

    PROCEDURE actualizar (
    p_id_movimiento IN MOVIMIENTO_INVENTARIO.id_movimiento%TYPE,
    p_tipo IN MOVIMIENTO_INVENTARIO.tipo%TYPE,
    p_cantidad IN MOVIMIENTO_INVENTARIO.cantidad%TYPE
) IS
            v_id INGREDIENTE.id_ingrediente%TYPE;
            v_bloqueo INGREDIENTE.id_ingrediente%TYPE;
            v_tipo_ant MOVIMIENTO_INVENTARIO.tipo%TYPE;
            v_nuevo_tipo MOVIMIENTO_INVENTARIO.tipo%TYPE;
            v_cantidad_ant NUMBER;
            v_disponible NUMBER;
            v_neto_anterior NUMBER;
            v_neto_nuevo NUMBER;
        BEGIN

            IF p_cantidad IS NULL OR p_cantidad<=0 OR p_tipo IS NULL OR UPPER(TRIM(p_tipo)) NOT IN
               ('ENTRADA','SALIDA','AJUSTE_ENTRADA','AJUSTE_SALIDA') THEN
                RAISE_APPLICATION_ERROR(-20217,'Tipo o cantidad de movimiento invalido');
            END IF;
            SELECT id_ingrediente INTO v_id FROM MOVIMIENTO_INVENTARIO
             WHERE id_movimiento=p_id_movimiento;
            SELECT id_ingrediente INTO v_bloqueo FROM INGREDIENTE
             WHERE id_ingrediente=v_id FOR UPDATE;
            SELECT tipo,cantidad INTO v_tipo_ant,v_cantidad_ant
              FROM MOVIMIENTO_INVENTARIO WHERE id_movimiento=p_id_movimiento FOR UPDATE;
            v_disponible := FN_EXISTENCIA_INGREDIENTE(v_id);
            v_nuevo_tipo := UPPER(TRIM(p_tipo));
            v_neto_anterior := CASE WHEN v_tipo_ant IN ('ENTRADA','AJUSTE_ENTRADA')
                                   THEN v_cantidad_ant ELSE -v_cantidad_ant END;
            v_neto_nuevo := CASE WHEN v_nuevo_tipo IN ('ENTRADA','AJUSTE_ENTRADA')
                                THEN p_cantidad ELSE -p_cantidad END;
            IF v_disponible - v_neto_anterior + v_neto_nuevo < 0 THEN
               RAISE_APPLICATION_ERROR(-20218,'Actualizacion generaria inventario negativo');
            END IF;
            UPDATE MOVIMIENTO_INVENTARIO
               SET tipo=v_nuevo_tipo,cantidad=p_cantidad
             WHERE id_movimiento=p_id_movimiento;
    END actualizar;

    PROCEDURE eliminar (
    p_id_movimiento IN MOVIMIENTO_INVENTARIO.id_movimiento%TYPE
) IS
            v_id INGREDIENTE.id_ingrediente%TYPE;
            v_bloqueo INGREDIENTE.id_ingrediente%TYPE;
            v_tipo MOVIMIENTO_INVENTARIO.tipo%TYPE;
            v_cantidad NUMBER;
            v_disponible NUMBER;
        BEGIN

            SELECT id_ingrediente INTO v_id FROM MOVIMIENTO_INVENTARIO
             WHERE id_movimiento=p_id_movimiento;
            SELECT id_ingrediente INTO v_bloqueo FROM INGREDIENTE
             WHERE id_ingrediente=v_id FOR UPDATE;
            SELECT tipo,cantidad INTO v_tipo,v_cantidad FROM MOVIMIENTO_INVENTARIO
             WHERE id_movimiento=p_id_movimiento FOR UPDATE;
            IF v_tipo IN ('ENTRADA','AJUSTE_ENTRADA') THEN
                v_disponible := FN_EXISTENCIA_INGREDIENTE(v_id);
                IF v_disponible-v_cantidad<0 THEN
                    RAISE_APPLICATION_ERROR(-20219,'Eliminar una entrada dejaria inventario negativo');
                END IF;
            END IF;
            DELETE FROM MOVIMIENTO_INVENTARIO WHERE id_movimiento=p_id_movimiento;
    END eliminar;

    FUNCTION existencia (
    p_id_ingrediente IN MOVIMIENTO_INVENTARIO.id_ingrediente%TYPE
) RETURN NUMBER IS
        BEGIN

            RETURN FN_EXISTENCIA_INGREDIENTE(p_id_ingrediente);
    END existencia;

END PKG_MOVIMIENTO_INV;
/

PROMPT ===== PKG_REPORTES_VENTAS =====
-- Preparamos reportes de consulta usando nuestras vistas y funciones.
CREATE OR REPLACE PACKAGE PKG_REPORTES_VENTAS AS
    PROCEDURE listar_ventas (
    p_desde IN DATE,
    p_hasta IN DATE,
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE productos_mas_vendidos (
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE pedidos_por_estado (
    p_estado IN PEDIDO.estado%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    FUNCTION total_ventas_periodo (
    p_desde IN DATE,
    p_hasta IN DATE
) RETURN NUMBER;
END PKG_REPORTES_VENTAS;
/
CREATE OR REPLACE PACKAGE BODY PKG_REPORTES_VENTAS AS
    PROCEDURE listar_ventas (
    p_desde IN DATE,
    p_hasta IN DATE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            IF p_desde IS NULL OR p_hasta IS NULL OR TRUNC(p_hasta)<TRUNC(p_desde) THEN
               RAISE_APPLICATION_ERROR(-20220,'Rango de fechas invalido');
            END IF;
            OPEN p_resultado FOR
              SELECT id_venta,id_pedido,fecha_venta,medio_pago,id_cliente,cliente,total_venta
                FROM V_VENTAS_RESUMEN
               WHERE fecha_venta>=TRUNC(p_desde) AND fecha_venta<TRUNC(p_hasta)+1
               ORDER BY fecha_venta,id_venta;
    END listar_ventas;

    PROCEDURE productos_mas_vendidos (
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR
              SELECT id_producto,producto,categoria,ventas_registradas,unidades_vendidas,ingreso_total
                FROM V_PRODUCTOS_MAS_VENDIDOS
               ORDER BY unidades_vendidas DESC,id_producto;
    END productos_mas_vendidos;

    PROCEDURE pedidos_por_estado (
    p_estado IN PEDIDO.estado%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR
              SELECT id_pedido,fecha_pedido,fecha_entrega,estado,cliente,empleado,total_pedido
                FROM V_PEDIDOS_RESUMEN
               WHERE p_estado IS NULL OR estado = p_estado
               ORDER BY fecha_pedido DESC,id_pedido;
    END pedidos_por_estado;

    FUNCTION total_ventas_periodo (
    p_desde IN DATE,
    p_hasta IN DATE
) RETURN NUMBER IS
        BEGIN

            RETURN FN_VENTAS_PERIODO(p_desde,p_hasta);
    END total_ventas_periodo;

END PKG_REPORTES_VENTAS;
/

PROMPT ===== PKG_REPORTES_INVENTARIO =====
-- Consultamos existencias, alertas y movimientos sin modificar los datos.
CREATE OR REPLACE PACKAGE PKG_REPORTES_INVENTARIO AS
    PROCEDURE listar_stock (
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE listar_alertas (
    p_resultado OUT SYS_REFCURSOR
);
    PROCEDURE kardex_ingrediente (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE,
    p_resultado OUT SYS_REFCURSOR
);
    FUNCTION total_alertas RETURN NUMBER;
    FUNCTION existencia (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) RETURN NUMBER;
END PKG_REPORTES_INVENTARIO;
/
CREATE OR REPLACE PACKAGE BODY PKG_REPORTES_INVENTARIO AS
    PROCEDURE listar_stock (
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR
              SELECT id_ingrediente,ingrediente,unidad_medida,stock_minimo,existencia,estado_stock
                FROM V_STOCK_INGREDIENTES ORDER BY ingrediente,id_ingrediente;
    END listar_stock;

    PROCEDURE listar_alertas (
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR
              SELECT id_ingrediente,ingrediente,unidad_medida,stock_minimo,existencia,estado_stock
                FROM V_ALERTAS_INVENTARIO ORDER BY ingrediente,id_ingrediente;
    END listar_alertas;

    PROCEDURE kardex_ingrediente (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE,
    p_resultado OUT SYS_REFCURSOR
) IS
        BEGIN

            OPEN p_resultado FOR
              SELECT id_movimiento,id_ingrediente,id_empleado,id_proveedor,id_produccion,
                     tipo,cantidad,fecha_movimiento
                FROM MOVIMIENTO_INVENTARIO
               WHERE p_id_ingrediente IS NULL OR id_ingrediente=p_id_ingrediente
               ORDER BY fecha_movimiento DESC,id_movimiento DESC;
    END kardex_ingrediente;

    FUNCTION total_alertas RETURN NUMBER IS
            v_total NUMBER;
        BEGIN

            SELECT COUNT(*) INTO v_total FROM V_ALERTAS_INVENTARIO;
            RETURN v_total;
    END total_alertas;

    FUNCTION existencia (
    p_id_ingrediente IN INGREDIENTE.id_ingrediente%TYPE
) RETURN NUMBER IS
        BEGIN

            RETURN FN_EXISTENCIA_INGREDIENTE(p_id_ingrediente);
    END existencia;

END PKG_REPORTES_INVENTARIO;
/

PROMPT ===== VERIFICACION: DIEZ PAQUETES Y DIEZ CUERPOS =====
SELECT object_name,object_type,status
FROM USER_OBJECTS
WHERE object_name IN (
    'PKG_PROVEEDOR_ING',
    'PKG_RECETA',
    'PKG_RECETA_ING',
    'PKG_PEDIDO',
    'PKG_DETALLE_PEDIDO',
    'PKG_PRODUCCION',
    'PKG_VENTA',
    'PKG_MOVIMIENTO_INV',
    'PKG_REPORTES_VENTAS',
    'PKG_REPORTES_INVENTARIO'
)
AND object_type IN ('PACKAGE','PACKAGE BODY')
ORDER BY object_name,object_type;

PROMPT ===== TOTAL DE ESPECIFICACIONES VALIDAS: ESPERADO 10 =====
SELECT COUNT(*) AS TOTAL_PAQUETES_VALIDOS
FROM USER_OBJECTS
WHERE object_type='PACKAGE' AND status='VALID'
AND object_name IN (
    'PKG_PROVEEDOR_ING',
    'PKG_RECETA',
    'PKG_RECETA_ING',
    'PKG_PEDIDO',
    'PKG_DETALLE_PEDIDO',
    'PKG_PRODUCCION',
    'PKG_VENTA',
    'PKG_MOVIMIENTO_INV',
    'PKG_REPORTES_VENTAS',
    'PKG_REPORTES_INVENTARIO'
);

PROMPT ===== TOTAL DE CUERPOS VALIDOS: ESPERADO 10 =====
SELECT COUNT(*) AS TOTAL_CUERPOS_VALIDOS
FROM USER_OBJECTS
WHERE object_type='PACKAGE BODY' AND status='VALID'
AND object_name IN (
    'PKG_PROVEEDOR_ING',
    'PKG_RECETA',
    'PKG_RECETA_ING',
    'PKG_PEDIDO',
    'PKG_DETALLE_PEDIDO',
    'PKG_PRODUCCION',
    'PKG_VENTA',
    'PKG_MOVIMIENTO_INV',
    'PKG_REPORTES_VENTAS',
    'PKG_REPORTES_INVENTARIO'
);

PROMPT ===== ERRORES DE COMPILACION: ESPERADO 0 FILAS =====
SELECT name,type,line,position,text
FROM USER_ERRORS
WHERE type IN ('PACKAGE','PACKAGE BODY')
AND name IN (
    'PKG_PROVEEDOR_ING',
    'PKG_RECETA',
    'PKG_RECETA_ING',
    'PKG_PEDIDO',
    'PKG_DETALLE_PEDIDO',
    'PKG_PRODUCCION',
    'PKG_VENTA',
    'PKG_MOVIMIENTO_INV',
    'PKG_REPORTES_VENTAS',
    'PKG_REPORTES_INVENTARIO'
)
ORDER BY name,type,sequence;

PROMPT ===== PRUEBAS DE SOLO LECTURA; SIN INSERTAR DATOS =====
SELECT PKG_REPORTES_INVENTARIO.TOTAL_ALERTAS() AS TOTAL_ALERTAS
FROM DUAL;
SELECT PKG_REPORTES_VENTAS.TOTAL_VENTAS_PERIODO(TRUNC(SYSDATE)-30,TRUNC(SYSDATE))
       AS VENTAS_PERIODO
FROM DUAL;

