# E-commerce Spring Boot API

Una aplicación e-commerce construida con Spring Boot 3.5.6, siguiendo principios SOLID y arquitectura por capas. Implementa un CRUD completo para gestión de usuarios con autenticación JWT y PostgreSQL como base de datos.

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**

## Arquitectura por Capas

```
src/main/java/com/springback/ecommerce_layers/
├── config/          # Configuraciones (Security, Database, CORS)
├── controller/      # Controllers REST (@RestController)
├── dto/            # Data Transfer Objects
│   ├── request/    # DTOs para requests
│   └── response/   # DTOs para responses
├── entity/         # Entidades JPA (@Entity)
├── exception/      # Manejo global de excepciones
├── repository/     # Repositorios JPA (@Repository)
└── service/        # Lógica de negocio (@Service)
    └── impl/       # Implementaciones de servicios
```

## Setup del Proyecto

### Prerrequisitos

- ☕ **Java 17 o superior**
- 🐋 **Docker** y **Docker Compose**
- 💻 **IDE** (IntelliJ IDEA recomendado)
- 🔧 **Git**

### Instalación Rápida

#### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd e-commerce_layers
```

#### 2. Configurar base de datos con Docker
```bash
# Windows
scripts\setup.bat

# Linux/Mac
chmod +x scripts/setup.sh
./scripts/setup.sh
```

#### 3. Ejecutar aplicación
- Abrir proyecto en IntelliJ IDEA
- Ejecutar `ECommerceLayersApplication.java`
- Aplicación disponible en: http://localhost:8080

## Solución de Problemas Comunes

### ⚠️ Error de Puerto Ocupado (5432)

**Problema**: `FATAL: la autentificación password falló para el usuario 'ecommerce_user'`

**Causa**: Tienes PostgreSQL instalado localmente ocupando el puerto 5432

**Solución**:
```bash
# Verificar puertos ocupados
netstat -an | findstr 5432

# Si ves múltiples líneas, cambiar puerto del contenedor
docker stop ecommerce-postgres
docker rm ecommerce-postgres
docker run -d --name ecommerce-postgres -e POSTGRES_DB=ecommerce_db -e POSTGRES_USER=ecommerce_user -e POSTGRES_PASSWORD=ecommerce_pass123 -p 5433:5432 postgres:15-alpine

# Actualizar application.properties
spring.datasource.url=jdbc:postgresql://localhost:5433/ecommerce_db
```

### ⚠️ Error de Lombok

**Problema**: `ClassNotFoundException: User$UserBuilder`

**Solución**:
1. Instalar plugin Lombok en IntelliJ: `File > Settings > Plugins > Lombok`
2. Habilitar annotation processing: `File > Settings > Build > Compiler > Annotation Processors > Enable annotation processing`
3. Limpiar proyecto: `Build > Clean Project > Rebuild Project`

### ⚠️ Docker no funciona

**Verificación**:
```bash
# Verificar Docker
docker --version
docker-compose --version

# Si no funciona, instalar Docker Desktop
```

### ⚠️ Puerto 8080 ocupado

**Solución**:
```bash
# Cambiar puerto en application.properties
server.port=8081
```

## Configuración

### Base de Datos

El proyecto se conecta a PostgreSQL con estas credenciales por defecto:

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=ecommerce_user
spring.datasource.password=ecommerce_pass123
```

### Usuario Administrador

Se crea automáticamente al iniciar la aplicación:
- **Usuario**: `admin`
- **Contraseña**: `admin123`
- **Rol**: `ADMIN`

## API Endpoints

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/users/login` | Autenticación | ❌ |
| GET | `/api/users/me` | Usuario actual | ✅ |
| GET | `/api/users` | Listar usuarios | ✅ |
| GET | `/api/users/{id}` | Usuario por ID | ✅ |
| POST | `/api/users` | Crear usuario | ✅ |
| PUT | `/api/users/{id}` | Actualizar usuario | ✅ |
| DELETE | `/api/users/{id}` | Eliminar usuario | ✅ |
| GET | `/api/users/active` | Usuarios activos | ✅ |
| GET | `/api/users/role/{role}` | Usuarios por rol | ✅ |

### Autenticación

Todos los endpoints (excepto login) requieren header:
```
Authorization: Bearer <jwt-token>
```

**Token válido por**: 10 horas

## Ejemplos de Uso

### 1. Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "firstLogin": false,
  "username": "admin",
  "role": "ADMIN"
}
```

### 2. Crear Usuario
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@email.com",
    "password": "password123",
    "phone": "987654321",
    "role": "CUSTOMER"
  }'
```

### 3. Listar Usuarios
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>"
```

## Modelo de Datos

### Usuario
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    active BOOLEAN NOT NULL DEFAULT true,
    enabled BOOLEAN NOT NULL DEFAULT true,
    first_login BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

**Roles disponibles**: `ADMIN`, `CUSTOMER`, `SELLER`

## Comandos Útiles

### Docker
```bash
# Ver contenedores activos
docker ps

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Parar base de datos
docker-compose down

# Reiniciar PostgreSQL
docker-compose restart postgres

# Limpiar todo (CUIDADO: elimina datos)
docker-compose down -v
```

### Maven
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Crear JAR
mvn clean package

# Ejecutar aplicación
mvn spring-boot:run
```

## Testing con Postman

Importa la colección desde el archivo de documentación con todos los endpoints configurados.

**Headers necesarios:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

## Seguridad

- **Autenticación**: JWT con expiración de 10 horas
- **Encriptación**: BCrypt para contraseñas
- **CORS**: Configurado para desarrollo (localhost:4200, localhost:3000)
- **Validaciones**: Bean Validation en DTOs

## Principios SOLID Implementados

- **S** - Single Responsibility: Cada clase tiene una responsabilidad específica
- **O** - Open/Closed: Extensible mediante interfaces
- **L** - Liskov Substitution: Implementaciones intercambiables
- **I** - Interface Segregation: Interfaces específicas
- **D** - Dependency Inversion: Uso de abstracciones

## Estructura de Respuestas

### Éxito
```json
{
  "id": 1,
  "firstName": "Admin",
  "lastName": "System",
  "email": "admin@ecommerce.com",
  "phone": "000000000",
  "role": "ADMIN",
  "active": true,
  "createdAt": "2025-09-20T18:30:00.000Z",
  "updatedAt": "2025-09-20T18:30:00.000Z"
}
```

### Error de Validación
```json
{
  "timestamp": "2025-09-20T18:40:16.933Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Error de validación en los campos",
  "validationErrors": {
    "email": "El email debe tener un formato válido",
    "password": "La contraseña debe tener entre 6 y 100 caracteres"
  }
}
```

### Error de Autenticación
```json
{
  "timestamp": "2025-09-20T18:40:16.933Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o expirado"
}
```

## Próximas Funcionalidades

- [ ] Entidad Product con CRUD
- [ ] Relaciones User-Product
- [ ] Sistema de roles más granular
- [ ] Paginación en listados
- [ ] Tests unitarios y de integración
- [ ] Documentación con Swagger/OpenAPI
- [ ] Métricas con Actuator

## Contribución

1. Fork del proyecto
2. Crear rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -am 'Agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

## Licencia

MIT License - ver archivo [LICENSE](LICENSE) para detalles

---

**Desarrollado con Spring Boot 3.5.6 siguiendo buenas prácticas y principios SOLID**