-- PANADERÍA AURORA - SEGUNDO AVANCE - 5 TRIGGERS
-- Trabajamos con Oracle Database 21c XE y el contenedor XEPDB1.
-- Usamos las tablas que creamos en Panaderia_Aurora_01_Crear_Tablas.sql.
-- Ejecutamos este archivo con F5 desde nuestra conexión PANADERIA_AURORA.
-- Creamos o actualizamos cinco triggers, sin agregar ni borrar registros.
-- Las validaciones se activarán cuando insertamos o cambiamos datos en sus tablas.
-- No confirmamos ni revertimos transacciones dentro de estos triggers.

SET SERVEROUTPUT ON

PROMPT ===== VERIFIQUE CONEXION: PANADERIA_AURORA / XEPDB1 =====
SELECT USER AS USUARIO_ACTUAL, SYS_CONTEXT('USERENV','CON_NAME') AS CONTENEDOR
FROM DUAL;

PROMPT ===== 1 DE 5: VALIDAR NOMBRE DE PRODUCTOS =====
CREATE OR REPLACE TRIGGER TRG_PA_PRODUCTO_NOMBRE
BEFORE INSERT OR UPDATE OF nombre ON PRODUCTO
FOR EACH ROW
BEGIN
    :NEW.nombre := TRIM(:NEW.nombre);
    IF :NEW.nombre IS NULL THEN
        RAISE_APPLICATION_ERROR(-20301,
            'El nombre del producto no puede estar vacio.');
    END IF;
END;
/

PROMPT ===== 2 DE 5: NORMALIZAR DATOS DE CLIENTES =====
CREATE OR REPLACE TRIGGER TRG_PA_CLIENTE_DATOS
BEFORE INSERT OR UPDATE OF nombre, telefono, correo ON CLIENTE
FOR EACH ROW
BEGIN
    :NEW.nombre := TRIM(:NEW.nombre);
    :NEW.telefono := TRIM(:NEW.telefono);
    :NEW.correo := LOWER(TRIM(:NEW.correo));
    IF :NEW.nombre IS NULL THEN
        RAISE_APPLICATION_ERROR(-20302,
            'El nombre del cliente no puede estar vacio.');
    END IF;
END;
/

PROMPT ===== 3 DE 5: NORMALIZAR DATOS DE PROVEEDORES =====
CREATE OR REPLACE TRIGGER TRG_PA_PROVEEDOR_DATOS
BEFORE INSERT OR UPDATE OF nombre, telefono, correo ON PROVEEDOR
FOR EACH ROW
BEGIN
    :NEW.nombre := TRIM(:NEW.nombre);
    :NEW.telefono := TRIM(:NEW.telefono);
    :NEW.correo := LOWER(TRIM(:NEW.correo));
    IF :NEW.nombre IS NULL THEN
        RAISE_APPLICATION_ERROR(-20303,
            'El nombre del proveedor no puede estar vacio.');
    END IF;
END;
/

PROMPT ===== 4 DE 5: VALIDAR FECHA DE ENTREGA DE PEDIDOS =====
CREATE OR REPLACE TRIGGER TRG_PA_PEDIDO_FECHAS
BEFORE INSERT OR UPDATE OF fecha_pedido, fecha_entrega ON PEDIDO
FOR EACH ROW
BEGIN
    -- Evitamos registrar entregas anteriores a la fecha del pedido.
    -- Comparamos las fechas por día, sin tomar en cuenta la hora.
    IF :NEW.fecha_entrega IS NOT NULL
       AND :NEW.fecha_pedido IS NOT NULL
       AND TRUNC(:NEW.fecha_entrega) < TRUNC(:NEW.fecha_pedido) THEN
        RAISE_APPLICATION_ERROR(-20304,
            'La fecha de entrega no puede preceder a la fecha del pedido.');
    END IF;
END;
/

PROMPT ===== 5 DE 5: NORMALIZAR Y VALIDAR EL TIPO DE MOVIMIENTO =====
CREATE OR REPLACE TRIGGER TRG_PA_MOVIMIENTO_TIPO
BEFORE INSERT OR UPDATE OF tipo ON MOVIMIENTO_INVENTARIO
FOR EACH ROW
BEGIN
    :NEW.tipo := UPPER(TRIM(:NEW.tipo));
    IF :NEW.tipo IS NULL OR :NEW.tipo NOT IN
       ('ENTRADA','SALIDA','AJUSTE_ENTRADA','AJUSTE_SALIDA') THEN
        RAISE_APPLICATION_ERROR(-20305,
            'El tipo de movimiento de inventario no es valido.');
    END IF;
END;
/

PROMPT ===== ESTADO DE LOS CINCO TRIGGERS =====
SELECT OBJECT_NAME AS TRIGGER_NOMBRE, STATUS
FROM USER_OBJECTS
WHERE OBJECT_TYPE = 'TRIGGER'
  AND OBJECT_NAME IN (
    'TRG_PA_PRODUCTO_NOMBRE',
    'TRG_PA_CLIENTE_DATOS',
    'TRG_PA_PROVEEDOR_DATOS',
    'TRG_PA_PEDIDO_FECHAS',
    'TRG_PA_MOVIMIENTO_TIPO'
  )
ORDER BY OBJECT_NAME;

PROMPT ===== TOTAL DE TRIGGERS VALIDOS (ESPERADO: 5) =====
SELECT COUNT(*) AS TOTAL_TRIGGERS_VALIDOS
FROM USER_OBJECTS
WHERE OBJECT_TYPE = 'TRIGGER'
  AND STATUS = 'VALID'
  AND OBJECT_NAME IN (
    'TRG_PA_PRODUCTO_NOMBRE',
    'TRG_PA_CLIENTE_DATOS',
    'TRG_PA_PROVEEDOR_DATOS',
    'TRG_PA_PEDIDO_FECHAS',
    'TRG_PA_MOVIMIENTO_TIPO'
  );

PROMPT ===== ERRORES DE COMPILACION (ESPERADO: 0 FILAS) =====
SELECT NAME, TYPE, LINE, POSITION, TEXT
FROM USER_ERRORS
WHERE TYPE = 'TRIGGER'
  AND NAME IN (
    'TRG_PA_PRODUCTO_NOMBRE',
    'TRG_PA_CLIENTE_DATOS',
    'TRG_PA_PROVEEDOR_DATOS',
    'TRG_PA_PEDIDO_FECHAS',
    'TRG_PA_MOVIMIENTO_TIPO'
  )
ORDER BY NAME, SEQUENCE;
