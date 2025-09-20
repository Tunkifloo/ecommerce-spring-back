-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Configuración de timezone
SET timezone = 'America/Lima';

-- Crear esquemas adicionales si es necesario
-- CREATE SCHEMA IF NOT EXISTS audit;

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE 'Base de datos ecommerce_db inicializada correctamente';
END
$$;