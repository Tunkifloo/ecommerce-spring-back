#!/bin/bash

echo "🚀 Configurando E-commerce Application..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir con color
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar Docker
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Verificar Docker Compose
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

# Crear directorios necesarios
print_status "Creando directorios..."
mkdir -p docker/postgres
mkdir -p logs
mkdir -p scripts

# Verificar que existe el archivo .env
if [ ! -f ".env" ]; then
    print_warning "Archivo .env no encontrado. Creando uno por defecto..."
    cat > .env << EOL
# Database Configuration
DB_NAME=ecommerce_db
DB_USER=ecommerce_user
DB_PASSWORD=ecommerce_pass123
DB_PORT=5432

# Application Configuration
APP_PORT=8080

# PgAdmin Configuration
PGADMIN_EMAIL=admin@ecommerce.com
PGADMIN_PASSWORD=admin123
PGLADMIN_PORT=8081

# Development Configuration
COMPOSE_PROJECT_NAME=ecommerce
EOL
    print_success "Archivo .env creado"
fi

# Parar servicios existentes
print_status "Parando servicios existentes..."
docker-compose down

# Limpiar volúmenes si se especifica
if [ "$1" = "--clean" ]; then
    print_warning "Limpiando volúmenes existentes..."
    docker-compose down -v
    docker volume prune -f
fi

# Construir y levantar servicios
print_status "Construyendo y levantando servicios..."
docker-compose up -d --build

# Esperar a que PostgreSQL esté listo
print_status "Esperando a que PostgreSQL esté listo..."
timeout=60
while [ $timeout -gt 0 ]; do
    if docker-compose exec postgres pg_isready -U ecommerce_user -d ecommerce_db &> /dev/null; then
        break
    fi
    echo -n "."
    sleep 2
    timeout=$((timeout-2))
done

if [ $timeout -le 0 ]; then
    print_error "PostgreSQL no está respondiendo después de 60 segundos"
    exit 1
fi

print_success "PostgreSQL está listo!"

# Mostrar información útil
echo ""
echo "🎉 Setup completado!"
echo ""
echo "📊 Servicios disponibles:"
echo "  - Aplicación: http://localhost:8080"
echo "  - PgAdmin: http://localhost:8081"
echo "    Email: admin@ecommerce.com"
echo "    Password: admin123"
echo ""
echo "🗄️  Base de datos:"
echo "  - Host: localhost"
echo "  - Puerto: 5432"
echo "  - Base de datos: ecommerce_db"
echo "  - Usuario: ecommerce_user"
echo "  - Password: ecommerce_pass123"
echo ""
echo "📋 Comandos útiles:"
echo "  - Ver logs: docker-compose logs -f"
echo "  - Parar servicios: docker-compose down"
echo "  - Reiniciar: docker-compose restart"
echo "  - Limpiar todo: docker-compose down -v"
echo ""

# Mostrar logs en tiempo real
print_status "Mostrando logs en tiempo real (Ctrl+C para salir)..."
docker-compose logs -f