#!/bin/bash

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
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

# Compose Project
COMPOSE_PROJECT_NAME=ecommerce
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
sleep 10
timeout=60
while [ $timeout -gt 0 ]; do
    if docker-compose exec -T postgres pg_isready -U ecommerce_user -d ecommerce_db &> /dev/null; then
        break
    fi
    echo -n "."
    sleep 3
    timeout=$((timeout-3))
done

if [ $timeout -le 0 ]; then
    echo
    echo -e "${RED}[ERROR]${NC} PostgreSQL no está respondiendo"
    exit 1
fi

echo
echo -e "${BLUE}[INFO]${NC} Verificando datos iniciales..."
sleep 2

# Mostrar información final
echo
echo -e "${PURPLE}========================================${NC}"
echo -e "${PURPLE}   🎉 E-commerce listo para usar!${NC}"
echo -e "${PURPLE}========================================${NC}"
echo
echo -e "${CYAN}📊 Servicios disponibles:${NC}"
echo "  - PostgreSQL: localhost:5432"
echo "  - PgAdmin:    http://localhost:8081"
echo
echo -e "${CYAN}🔐 Credenciales PostgreSQL:${NC}"
echo "  - Base datos: ecommerce_db"
echo "  - Usuario:    ecommerce_user"
echo "  - Password:   ecommerce_pass123"
echo
echo -e "${CYAN}🔐 Credenciales PgAdmin:${NC}"
echo "  - Email:      admin@ecommerce.com"
echo "  - Password:   admin123"
echo
echo -e "${YELLOW}👥 USUARIOS DE PRUEBA CREADOS:${NC}"
echo "  - Admin:      admin / admin123"
echo "  - Customer:   ana.rodriguez@email.com / password123"
echo "  - Seller:     carlos.seller@email.com / password123"
echo
echo -e "${YELLOW}🛍️ PRODUCTOS DE PRUEBA:${NC}"
echo "  - 10 productos tecnológicos listos"
echo "  - MacBook Pro, iPhone 15, PlayStation 5, etc."
echo "  - Todos asignados al vendedor Carlos"
echo
echo -e "${GREEN}🚀 SIGUIENTE PASO:${NC}"
echo "  1. Abre tu IDE (IntelliJ IDEA)"
echo "  2. Ejecuta ECommerceLayersApplication.java"
echo "  3. App estará en: http://localhost:8080"
echo
echo -e "${GREEN}🧪 PRUEBA LA API:${NC}"
echo "  1. POST /api/users/login (con cualquier usuario)"
echo "  2. GET /api/products (ver productos disponibles)"
echo "  3. POST /api/products (crear como seller)"
echo
echo -e "${CYAN}📋 Comandos útiles:${NC}"
echo "  - Ver logs:       docker-compose logs -f"
echo "  - Parar BD:       docker-compose down"
echo "  - Reiniciar BD:   docker-compose restart"
echo "  - Limpiar todo:   docker-compose down -v"
echo