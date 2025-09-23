-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Configuración de timezone
SET timezone = 'America/Lima';

-- Solo mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'Base de datos ecommerce_db inicializada correctamente';
    RAISE NOTICE 'Spring Boot creará las tablas y datos iniciales';
    RAISE NOTICE '==============================================';
END
$$;