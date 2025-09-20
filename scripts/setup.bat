@echo off
echo 🚀 Configurando E-commerce Application...

REM Verificar Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker no está instalado. Por favor instala Docker primero.
    pause
    exit /b 1
)

REM Verificar Docker Compose
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose no está instalado. Por favor instala Docker Compose primero.
    pause
    exit /b 1
)

REM Crear directorios necesarios
echo [INFO] Creando directorios...
if not exist "docker\postgres" mkdir docker\postgres
if not exist "logs" mkdir logs
if not exist "scripts" mkdir scripts

REM Verificar archivo .env
if not exist ".env" (
    echo [WARNING] Archivo .env no encontrado. Creando uno por defecto...
    (
        echo # Database Configuration
        echo DB_NAME=ecommerce_db
        echo DB_USER=ecommerce_user
        echo DB_PASSWORD=ecommerce_pass123
        echo DB_PORT=5432
        echo.
        echo # Application Configuration
        echo APP_PORT=8080
        echo.
        echo # PgAdmin Configuration
        echo PGADMIN_EMAIL=admin@ecommerce.com
        echo PGADMIN_PASSWORD=admin123
        echo PGADMIN_PORT=8081
        echo.
        echo # Development Configuration
        echo COMPOSE_PROJECT_NAME=ecommerce
    ) > .env
    echo [SUCCESS] Archivo .env creado
)

REM Parar servicios existentes
echo [INFO] Parando servicios existentes...
docker-compose down

REM Construir y levantar servicios
echo [INFO] Construyendo y levantando servicios...
docker-compose up -d --build

echo.
echo 🎉 Setup completado!
echo.
echo 📊 Servicios disponibles:
echo   - Aplicación: http://localhost:8080
echo   - PgAdmin: http://localhost:8081
echo     Email: admin@ecommerce.com
echo     Password: admin123
echo.
echo 🗄️ Base de datos:
echo   - Host: localhost
echo   - Puerto: 5432
echo   - Base de datos: ecommerce_db
echo   - Usuario: ecommerce_user
echo   - Password: ecommerce_pass123
echo.
echo 📋 Comandos útiles:
echo   - Ver logs: docker-compose logs -f
echo   - Parar servicios: docker-compose down
echo   - Reiniciar: docker-compose restart
echo   - Limpiar todo: docker-compose down -v
echo.

pause