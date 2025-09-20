#!/bin/bash

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
RED='\033[0;31m'
NC='\033[0m'

clear
echo -e "${PURPLE}========================================${NC}"
echo -e "${PURPLE}   🚀 E-commerce Database Setup${NC}"
echo -e "${PURPLE}========================================${NC}"
echo

# Verificar Docker
echo -e "${BLUE}[INFO]${NC} Verificando Docker..."
if ! command -v docker &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} Docker no encontrado. Instala Docker desde:"
    echo "https://docs.docker.com/get-docker/"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} Docker Compose no encontrado."
    exit 1
fi

echo -e "${GREEN}[SUCCESS]${NC} Docker encontrado"

# Crear directorios
echo
echo -e "${BLUE}[INFO]${NC} Creando directorios..."
mkdir -p docker/postgres
mkdir -p logs

# Crear .env si no existe
echo -e "${BLUE}[INFO]${NC} Verificando archivo .env..."
if [ ! -f ".env" ]; then
    echo -e "${BLUE}[INFO]${NC} Creando archivo .env..."
    cat > .env << EOL
# Database Configuration
DB_NAME=ecommerce_db
DB_USER=ecommerce_user
DB_PASSWORD=ecommerce_pass123
DB_PORT=5432

# PgAdmin Configuration
PGADMIN_EMAIL=admin@ecommerce.com
PGADMIN_PASSWORD=admin123
PGADMIN_PORT=8081
EOL
    echo -e "${GREEN}[SUCCESS]${NC} Archivo .env creado"
fi

# Parar servicios existentes
echo
echo -e "${BLUE}[INFO]${NC} Parando servicios existentes..."
docker-compose down 2>/dev/null

# Iniciar servicios
echo -e "${BLUE}[INFO]${NC} Iniciando PostgreSQL y PgAdmin..."
docker-compose up -d

# Esperar PostgreSQL
echo -e "${BLUE}[INFO]${NC} Esperando a que PostgreSQL esté listo..."
sleep 5
timeout=60
while [ $timeout -gt 0 ]; do
    if docker-compose exec -T postgres pg_isready -U ecommerce_user -d ecommerce_db &> /dev/null; then
        break
    fi
    echo -n "."
    sleep 2
    timeout=$((timeout-2))
done

if [ $timeout -le 0 ]; then
    echo
    echo -e "${RED}[ERROR]${NC} PostgreSQL no está respondiendo"
    exit 1
fi

# Mostrar información final
echo
echo -e "${PURPLE}========================================${NC}"
echo -e "${PURPLE}   🎉 Base de datos lista!${NC}"
echo -e "${PURPLE}========================================${NC}"
echo
echo "📊 Servicios disponibles:"
echo "  - PostgreSQL: localhost:5432"
echo "  - PgAdmin:    http://localhost:8081"
echo
echo "🔐 Credenciales PostgreSQL:"
echo "  - Base datos: ecommerce_db"
echo "  - Usuario:    ecommerce_user"
echo "  - Password:   ecommerce_pass123"
echo
echo "🔐 Credenciales PgAdmin:"
echo "  - Email:      admin@ecommerce.com"
echo "  - Password:   admin123"
echo
echo "🚀 SIGUIENTE PASO:"
echo "  1. Abre tu IDE (IntelliJ IDEA)"
echo "  2. Ejecuta ECommerceLayersApplication.java"
echo "  3. App estará en: http://localhost:8080"
echo
echo "📋 Comandos útiles:"
echo "  - Ver logs:       docker-compose logs -f"
echo "  - Parar BD:       docker-compose down"
echo "  - Reiniciar BD:   docker-compose restart"
echo