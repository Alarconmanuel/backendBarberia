-- Migración: agregar columna especialidad a tabla servicio
-- Ejecutar si no se usa ddl-auto=update (Hibernate lo crea automáticamente)
ALTER TABLE servicio ADD COLUMN IF NOT EXISTS especialidad VARCHAR(100);
