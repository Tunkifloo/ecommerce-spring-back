@echo off
echo.
echo ========================================
echo   🚀 E-commerce Database Setup
echo ========================================
echo.

echo [INFO] Verificando Docker...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker no encontrado. Instala Docker Desktop desde:
    echo https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
echo [SUCCESS] Docker encontrado

echo.
echo [INFO] Creando directorios...
if not exist "docker\postgres" mkdir docker\postgres
if not exist "logs" mkdir logs

echo [INFO] Verificando archivo .env...
if not exist ".env" (
    echo [INFO] Creando archivo .env...
    (
        echo # Database Configuration
        echo DB_NAME=ecommerce_db
        echo DB_USER=ecommerce_user
        echo DB_PASSWORD=ecommerce_pass123
        echo DB_PORT=5433
        echo.
        echo # PgAdmin Configuration
        echo PGADMIN_EMAIL=admin@ecommerce.com
        echo PGADMIN_PASSWORD=admin123
        echo PGADMIN_PORT=8081
        echo.
        echo # Compose Project
        echo COMPOSE_PROJECT_NAME=ecommerce
    ) > .env
    echo [SUCCESS] Archivo .env creado
)

echo.
echo [INFO] Parando servicios existentes...
docker-compose down -v 2>nul

echo [INFO] Iniciando PostgreSQL y PgAdmin...
docker-compose up -d

echo [INFO] Esperando a que PostgreSQL esté listo...
timeout /t 15 /nobreak >nul

echo [INFO] Verificando estado de contenedores...
docker-compose ps

echo [INFO] Verificando logs de PostgreSQL...
docker-compose logs postgres | findstr "database system is ready"

echo [INFO] Verificando conexión a la base de datos...
docker-compose exec -T postgres pg_isready -U ecommerce_user -d ecommerce_db

echo.
echo ========================================
echo   🎉 E-commerce listo para usar!
echo ========================================
echo.
echo 📊 Servicios disponibles:
echo   - PostgreSQL: localhost:5433
echo   - PgAdmin:    http://localhost:8081
echo.
echo 🔐 Credenciales PostgreSQL:
echo   - Base datos: ecommerce_db
echo   - Usuario:    ecommerce_user
echo   - Password:   ecommerce_pass123
echo   - Puerto:     5433
echo.
echo 🔐 Credenciales PgAdmin:
echo   - Email:      admin@ecommerce.com
echo   - Password:   admin123
echo   - URL:        http://localhost:8081
echo.
echo 👥 USUARIOS DE PRUEBA CREADOS:
echo   - Admin:      admin / admin123
echo   - Customer:   ana.rodriguez@email.com / password123
echo   - Seller:     carlos.seller@email.com / password123
echo.
echo 🛍️ PRODUCTOS DE PRUEBA:
echo   - 10 productos tecnológicos listos
echo   - MacBook Pro, iPhone 15, PlayStation 5, etc.
echo   - Todos asignados al vendedor Carlos
echo.
echo 🚀 SIGUIENTE PASO:
echo   1. Abre tu IDE (IntelliJ IDEA)
echo   2. Ejecuta ECommerceLayersApplication.java
echo   3. App estará en: http://localhost:8080
echo.
echo 🔧 CONFIGURAR PGADMIN:
echo   1. Ve a http://localhost:8081
echo   2. Login con admin@ecommerce.com / admin123
echo   3. Add New Server con estos datos:
echo      - Name: Ecommerce DB
echo      - Host: postgres (nombre del contenedor)
echo      - Port: 5432 (puerto interno)
echo      - Database: ecommerce_db
echo      - Username: ecommerce_user
echo      - Password: ecommerce_pass123
echo.
echo 🧪 COMANDOS DE VERIFICACIÓN:
echo   - Ver contenedores:   docker-compose ps
echo   - Ver logs:           docker-compose logs -f
echo   - Conectar a BD:      docker-compose exec postgres psql -U ecommerce_user -d ecommerce_db
echo   - Parar servicios:    docker-compose down
echo   - Limpiar todo:       docker-compose down -v
echo.
pause