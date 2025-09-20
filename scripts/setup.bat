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
        echo DB_PORT=5432
        echo.
        echo # PgAdmin Configuration
        echo PGADMIN_EMAIL=admin@ecommerce.com
        echo PGADMIN_PASSWORD=admin123
        echo PGADMIN_PORT=8081
    ) > .env
    echo [SUCCESS] Archivo .env creado
)

echo.
echo [INFO] Parando servicios existentes...
docker-compose down 2>nul

echo [INFO] Iniciando PostgreSQL y PgAdmin...
docker-compose up -d

echo [INFO] Esperando a que PostgreSQL esté listo...
timeout /t 5 /nobreak >nul
:wait_loop
docker-compose exec -T postgres pg_isready -U ecommerce_user -d ecommerce_db >nul 2>&1
if %errorlevel% equ 0 goto ready
timeout /t 2 /nobreak >nul
goto wait_loop

:ready
echo.
echo ========================================
echo   🎉 Base de datos lista!
echo ========================================
echo.
echo 📊 Servicios disponibles:
echo   - PostgreSQL: localhost:5432
echo   - PgAdmin:    http://localhost:8081
echo.
echo 🔐 Credenciales PostgreSQL:
echo   - Base datos: ecommerce_db
echo   - Usuario:    ecommerce_user
echo   - Password:   ecommerce_pass123
echo.
echo 🔐 Credenciales PgAdmin:
echo   - Email:      admin@ecommerce.com
echo   - Password:   admin123
echo.
echo 🚀 SIGUIENTE PASO:
echo   1. Abre tu IDE (IntelliJ IDEA)
echo   2. Ejecuta ECommerceLayersApplication.java
echo   3. App estará en: http://localhost:8080
echo.
echo 📋 Comandos útiles:
echo   - Ver logs:       docker-compose logs -f
echo   - Parar BD:       docker-compose down
echo   - Reiniciar BD:   docker-compose restart
echo.
pause