# E-commerce Spring Boot API

Una aplicación e-commerce construida con Spring Boot 3.5.6, siguiendo principios SOLID y arquitectura por capas. Implementa un CRUD completo para gestión de usuarios y productos con autenticación JWT, relaciones entre entidades, datos de prueba y PostgreSQL como base de datos.

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**
- **Cache (Simple)**

## Arquitectura por Capas

```
src/main/java/com/springback/ecommerce_layers/
├── config/          # Configuraciones (Security, Database, CORS, Cache)
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

## Modelo de Datos y Relaciones

### Entidades

#### Usuario (User)
- **ID**: Identificador único
- **Datos personales**: firstName, lastName, email, phone
- **Credenciales**: username, password (BCrypt)
- **Roles**: ADMIN, CUSTOMER, SELLER
- **Estado**: active, enabled, firstLogin
- **Auditoría**: createdAt, updatedAt

#### Producto (Product)
- **ID**: Identificador único
- **Información**: name, description, price (BigDecimal), stock
- **Estado**: active
- **Relación**: seller (ManyToOne con User)
- **Auditoría**: createdAt, updatedAt

### Relaciones

- **User → Product**: Un usuario (SELLER/ADMIN) puede tener múltiples productos (OneToMany)
- **Product → User**: Cada producto pertenece a un vendedor específico (ManyToOne)

```sql
-- Estructura de tablas con relaciones
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

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    seller_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id)
);
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

## 🎯 Datos de Prueba Incluidos

Al ejecutar los scripts de instalación, se crean automáticamente:

### 👥 Usuarios de Prueba

| Usuario | Username | Password | Rol | Descripción |
|---------|-------|----------|-----|-------------|
| **Admin** | admin | admin123 | ADMIN | Usuario administrador (creado por la app) |
| **Ana Rodríguez** | ana.rodriguez | password123 | CUSTOMER | Cliente de ejemplo |
| **Carlos Mendoza** | carlos.seller | password123 | SELLER | Vendedor con productos |

### 🛍️ Productos Tecnológicos de Prueba

El vendedor **Carlos Mendoza** tiene 10 productos creados:

#### 💻 Laptops
1. **MacBook Pro M3 14"** - $2,499.99 (5 unidades)
2. **Dell XPS 13 Plus** - $1,899.99 (8 unidades)
3. **ASUS ROG Strix G15** - $1,299.99 (12 unidades)

#### 📱 Smartphones
4. **iPhone 15 Pro Max** - $1,399.99 (15 unidades)
5. **Samsung Galaxy S24 Ultra** - $1,299.99 (10 unidades)
6. **Google Pixel 8 Pro** - $999.99 (20 unidades)

#### 🎧 Accesorios
7. **AirPods Pro 2da Gen** - $249.99 (25 unidades)
8. **Sony WH-1000XM5** - $399.99 (18 unidades)

#### 🎮 Gaming
9. **PlayStation 5 Slim** - $499.99 (7 unidades)
10. **Steam Deck OLED** - $649.99 (6 unidades)

## API Endpoints

### Autenticación
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/users/login` | Autenticación | ❌ |
| GET | `/api/users/me` | Usuario actual | ✅ |

### Usuarios
| Método | Endpoint | Descripción | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `/api/users` | Listar usuarios | ✅ | ADMIN |
| GET | `/api/users/{id}` | Usuario por ID | ✅ | ADMIN |
| POST | `/api/users` | Crear usuario | ✅ | ADMIN |
| PUT | `/api/users/{id}` | Actualizar usuario | ✅ | ADMIN |
| DELETE | `/api/users/{id}` | Eliminar usuario | ✅ | ADMIN |
| GET | `/api/users/active` | Usuarios activos | ✅ | ADMIN |
| GET | `/api/users/role/{role}` | Usuarios por rol | ✅ | ADMIN |

### Productos
| Método | Endpoint | Descripción | Auth | Roles |
|--------|----------|-------------|------|-------|
| GET | `/api/products` | Listar productos activos | ✅ | Todos |
| GET | `/api/products/available` | Productos disponibles | ✅ | Todos |
| GET | `/api/products/{id}` | Producto por ID | ✅ | Todos |
| GET | `/api/products/seller/{id}` | Productos por vendedor | ✅ | Todos |
| GET | `/api/products/search?name=xxx` | Buscar por nombre | ✅ | Todos |
| POST | `/api/products` | Crear producto | ✅ | ADMIN, SELLER |
| PUT | `/api/products/{id}` | Actualizar producto | ✅ | ADMIN, SELLER |
| DELETE | `/api/products/{id}` | Eliminar producto | ✅ | ADMIN |

## 🧪 Ejemplos de Uso con Datos de Prueba

### 1. Login como Customer
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ana.rodriguez@email.com",
    "password": "password123"
  }'
```

### 2. Login como Seller
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "carlos.seller@email.com",
    "password": "password123"
  }'
```

### 3. Ver todos los productos (como cualquier usuario)
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>"
```

### 4. Buscar productos por nombre
```bash
# Buscar iPhones
curl -X GET "http://localhost:8080/api/products/search?name=iPhone" \
  -H "Authorization: Bearer <token>"

# Buscar laptops
curl -X GET "http://localhost:8080/api/products/search?name=MacBook" \
  -H "Authorization: Bearer <token>"
```

### 5. Ver productos del vendedor Carlos
```bash
# Primero obtener el ID del vendedor (será 3 típicamente)
curl -X GET http://localhost:8080/api/products/seller/3 \
  -H "Authorization: Bearer <token>"
```

### 6. Crear nuevo producto (como seller)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seller-token>" \
  -d '{
    "name": "iPad Pro M3",
    "description": "Nueva iPad Pro con chip M3 y pantalla OLED",
    "price": 1199.99,
    "stock": 15,
    "sellerId": 3
  }'
```

### 7. Actualizar stock de producto
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seller-token>" \
  -d '{
    "stock": 3
  }'
```

## 🔧 Flujo de Pruebas Recomendado

### Paso 1: Verificar usuarios creados
```bash
# Login como admin
POST /api/users/login
{
  "username": "admin",
  "password": "admin123"
}

# Ver todos los usuarios
GET /api/users
Authorization: Bearer <admin-token>
```

### Paso 2: Explorar productos como customer
```bash
# Login como customer
POST /api/users/login
{
  "username": "ana.rodriguez",
  "password": "password123"
}

# Ver productos disponibles
GET /api/products/available
Authorization: Bearer <customer-token>
```

### Paso 3: Gestionar productos como seller
```bash
# Login como seller
POST /api/users/login
{
  "username": "carlos.seller",
  "password": "password123"
}

# Ver mis productos
GET /api/products/seller/{seller-id}
Authorization: Bearer <seller-token>

# Crear nuevo producto
POST /api/products
Authorization: Bearer <seller-token>
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

### Usuarios Creados Automáticamente

#### Por la aplicación:
- **Usuario**: `admin`
- **Contraseña**: `admin123`
- **Rol**: `ADMIN`
- **Customer**: `ana.rodriguez@email.com` / `password123`
- **Seller**: `carlos.seller@email.com` / `password123`

## Funcionalidades Implementadas

### Gestión de Usuarios
- ✅ CRUD completo de usuarios
- ✅ Autenticación JWT
- ✅ Roles y permisos
- ✅ Validaciones de negocio
- ✅ Encriptación de contraseñas
- ✅ **Usuarios de prueba preconfigurados**

### Gestión de Productos
- ✅ CRUD completo de productos
- ✅ Relación con vendedores
- ✅ Control de stock
- ✅ Búsqueda por nombre
- ✅ Filtros por estado y disponibilidad
- ✅ Validaciones de negocio
- ✅ Soft delete (desactivación)
- ✅ **Catálogo de productos tecnológicos precargado**

### Características Técnicas
- ✅ Cache implementado
- ✅ Transacciones
- ✅ Auditoría de entidades
- ✅ Manejo global de excepciones
- ✅ Validaciones Bean Validation
- ✅ Control de acceso por roles
- ✅ Documentación con logs
- ✅ **Datos de prueba automáticos**

## 📊 Información de los Datos de Prueba

### Distribución por Categorías
```
💻 Laptops (3):          $5,699.97 total
📱 Smartphones (3):      $3,699.97 total  
🎧 Accesorios (2):       $649.98 total
🎮 Gaming (2):           $1,149.98 total
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Total productos: 10
💰 Valor total stock: $11,199.90
📈 Stock total: 131 unidades
```

### Productos más populares (stock alto)
1. **AirPods Pro 2da Gen** - 25 unidades
2. **Google Pixel 8 Pro** - 20 unidades
3. **Sony WH-1000XM5** - 18 unidades
4. **iPhone 15 Pro Max** - 15 unidades

### Productos premium (precio alto)
1. **MacBook Pro M3 14"** - $2,499.99
2. **Dell XPS 13 Plus** - $1,899.99
3. **iPhone 15 Pro Max** - $1,399.99
4. **Samsung Galaxy S24 Ultra** - $1,299.99

## 🛠️ Comandos Útiles de Testing

### Verificar datos iniciales
```bash
# Contar usuarios por rol
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin-token>" | jq 'group_by(.role) | map({role: .[0].role, count: length})'

# Contar productos por vendedor
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" | jq 'group_by(.seller.id) | map({seller: .[0].seller.fullName, count: length})'

# Ver estadísticas de stock
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" | jq '[.[] | .stock] | add'
```

### Búsquedas de ejemplo
```bash
# Productos de gaming
curl -X GET "http://localhost:8080/api/products/search?name=PlayStation" \
  -H "Authorization: Bearer <token>"

# Productos Apple
curl -X GET "http://localhost:8080/api/products/search?name=Apple" \
  -H "Authorization: Bearer <token>"

# Laptops
curl -X GET "http://localhost:8080/api/products/search?name=MacBook" \
  -H "Authorization: Bearer <token>"
```

## Principios SOLID Implementados

### Single Responsibility Principle (SRP)
- **Controllers**: Solo manejan requests HTTP
- **Services**: Solo lógica de negocio
- **Repositories**: Solo acceso a datos
- **DTOs**: Solo transferencia de datos

### Open/Closed Principle (OCP)
- Interfaces para servicios permiten extensión
- Nuevas funcionalidades sin modificar código existente

### Liskov Substitution Principle (LSP)
- Implementaciones de servicios son intercambiables
- Polimorfismo en repositorios JPA

### Interface Segregation Principle (ISP)
- Interfaces específicas por funcionalidad
- No dependencias innecesarias

### Dependency Inversion Principle (DIP)
- Dependencia de abstracciones (interfaces)
- Inyección de dependencias con Spring

## Estructura de Respuestas

### Producto con Vendedor
```json
{
  "id": 1,
  "name": "MacBook Pro M3 14\"",
  "description": "Laptop Apple MacBook Pro de 14 pulgadas con chip M3, 16GB RAM, 512GB SSD. Perfecta para profesionales creativos y desarrolladores.",
  "price": 2499.99,
  "stock": 5,
  "active": true,
  "available": true,
  "seller": {
    "id": 3,
    "fullName": "Carlos Mendoza",
    "email": "carlos.seller@email.com"
  },
  "createdAt": "2025-09-23T10:30:00",
  "updatedAt": "2025-09-23T10:30:00"
}
```

### Lista de Usuarios de Prueba
```json
[
  {
    "id": 2,
    "firstName": "Ana",
    "lastName": "Rodríguez", 
    "email": "ana.rodriguez@email.com",
    "username": "ana.rodriguez",
    "role": "CUSTOMER",
    "active": true,
    "fullName": "Ana Rodríguez"
  },
  {
    "id": 3,
    "firstName": "Carlos",
    "lastName": "Mendoza",
    "email": "carlos.seller@email.com", 
    "username": "carlos.seller",
    "role": "SELLER",
    "active": true,
    "fullName": "Carlos Mendoza"
  }
]
```

## Validaciones de Negocio

### Productos
- ✅ Nombre único por vendedor
- ✅ Precio mayor a 0 (BigDecimal)
- ✅ Stock no negativo
- ✅ Solo SELLER/ADMIN pueden crear productos
- ✅ Descripción hasta 500 caracteres
- ✅ **Validación de vendedor existente y activo**

### Usuarios
- ✅ Email único
- ✅ Username único
- ✅ Validación de roles
- ✅ Contraseña encriptada (BCrypt)
- ✅ Formato de email válido
- ✅ **Datos de prueba con contraseñas hasheadas**

## Solución de Problemas Comunes

### ⚠️ Error de Datos Iniciales

**Problema**: No se crean los datos de prueba

**Solución**:
```bash
# Verificar que el script SQL se ejecutó
docker-compose logs postgres

# Si no se crearon, recrear la BD
docker-compose down -v
docker-compose up -d

# Verificar usuarios creados
docker-compose exec postgres psql -U ecommerce_user -d ecommerce_db -c "SELECT email, role FROM users;"

# Verificar productos creados  
docker-compose exec postgres psql -U ecommerce_user -d ecommerce_db -c "SELECT name, price FROM products;"
```

### ⚠️ Error de Login con Usuarios de Prueba

**Problema**: `Invalid credentials` con usuarios de prueba

**Causa**: Las contraseñas en el SQL están hasheadas para `password123`

**Solución**:
- Usar exactamente: `password123` (no `admin123`)
- Para admin usar: `admin123` (creado por la app)
- Verificar que se use el email completo como username

### ⚠️ Error al Crear Productos

**Problema**: Error `Vendedor no encontrado`

**Causa**: sellerId incorrecto en el request

**Solución**:
```bash
# Obtener ID correcto del vendedor Carlos
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin-token>" | jq '.[] | select(.role=="SELLER")'

# Usar el ID correcto (típicamente 3) en el request
```

## 🧪 Guía de Testing Completa

### Fase 1: Verificación Inicial
```bash
# 1. Verificar que los servicios están corriendo
docker-compose ps

# 2. Verificar datos en BD
docker-compose exec postgres psql -U ecommerce_user -d ecommerce_db -c "\dt"

# 3. Login como admin
POST /api/users/login {"username": "admin", "password": "admin123"}

# 4. Ver todos los usuarios
GET /api/users (con token admin)
```

### Fase 2: Testing como Customer
```bash
# 1. Login como customer
POST /api/users/login {"username": "ana.rodriguez@email.com", "password": "password123"}

# 2. Ver catálogo de productos
GET /api/products (con token customer)

# 3. Buscar productos específicos
GET /api/products/search?name=iPhone (con token customer)

# 4. Ver productos disponibles  
GET /api/products/available (con token customer)
```

### Fase 3: Testing como Seller
```bash
# 1. Login como seller
POST /api/users/login {"username": "carlos.seller@email.com", "password": "password123"}

# 2. Ver mis productos
GET /api/products/seller/{seller-id} (con token seller)

# 3. Crear nuevo producto
POST /api/products (con token seller)

# 4. Actualizar producto existente
PUT /api/products/{id} (con token seller)
```

### Fase 4: Testing Avanzado
```bash
# 1. Búsquedas complejas
GET /api/products/search?name=gaming
GET /api/products/search?name=pro

# 2. Testing de permisos (debería fallar)
DELETE /api/products/1 (con token customer) # Error 403

# 3. Testing de validaciones
POST /api/products con datos inválidos # Error 400
```

## Comandos Útiles

### Docker
```bash
# Ver contenedores activos
docker ps

# Ver logs de la BD
docker-compose logs -f postgres

# Conectar a la BD directamente
docker-compose exec postgres psql -U ecommerce_user -d ecommerce_db

# Reiniciar servicios
docker-compose restart

# Limpiar y recrear todo
docker-compose down -v && docker-compose up -d
```

### Consultas SQL Útiles
```sql
-- Ver todos los usuarios
SELECT id, first_name, last_name, email, role FROM users;

-- Ver productos con vendedor
SELECT p.name, p.price, p.stock, u.first_name, u.last_name 
FROM products p 
JOIN users u ON p.seller_id = u.id;

-- Contar productos por vendedor
SELECT u.first_name, u.last_name, COUNT(p.id) as productos
FROM users u 
LEFT JOIN products p ON u.id = p.seller_id 
GROUP BY u.id, u.first_name, u.last_name;

-- Valor total del inventario
SELECT SUM(price * stock) as valor_total FROM products WHERE active = true;
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

### Collection de Pruebas Sugerida

#### Environment Variables:
```json
{
  "baseUrl": "http://localhost:8080",
  "adminToken": "",
  "customerToken": "",
  "sellerToken": "",
  "sellerId": "3"
}
```

#### Tests Automatizados:
1. **Auth Tests**: Login con todos los usuarios
2. **Product Tests**: CRUD completo de productos
3. **Search Tests**: Búsquedas por nombre
4. **Permission Tests**: Verificar control de acceso
5. **Data Validation Tests**: Validaciones de entrada

## Seguridad

- **Autenticación**: JWT con expiración de 10 horas
- **Autorización**: Control de acceso por roles
- **Encriptación**: BCrypt para contraseñas (incluye datos de prueba)
- **CORS**: Configurado para desarrollo
- **Validaciones**: Bean Validation en DTOs
- **Soft Delete**: Los productos se desactivan, no se eliminan
- **Datos Seguros**: Contraseñas de prueba hasheadas en SQL

## Próximas Funcionalidades

- [ ] Entidad Order (Pedidos)
- [ ] Relaciones User-Order-Product
- [ ] Sistema de categorías de productos
- [ ] Paginación en listados
- [ ] Tests unitarios y de integración
- [ ] Documentación con Swagger/OpenAPI
- [ ] Métricas con Actuator
- [ ] Upload de imágenes de productos
- [ ] Sistema de reviews y ratings
- [ ] **Más datos de prueba**: categorías, órdenes, reviews

## Buenas Prácticas Implementadas

### Arquitectura
- ✅ Separación de responsabilidades por capas
- ✅ Principios SOLID aplicados
- ✅ Inyección de dependencias
- ✅ Manejo centralizado de excepciones

### Base de Datos
- ✅ Relaciones con integridad referencial
- ✅ Índices en campos de búsqueda
- ✅ Auditoría con timestamps
- ✅ Soft delete para datos sensibles
- ✅ **Datos de prueba realistas y consistentes**

### Seguridad
- ✅ Autenticación JWT
- ✅ Autorización por roles
- ✅ Validación de entrada
- ✅ Encriptación de passwords
- ✅ **Contraseñas de prueba seguras**

### Performance
- ✅ Cache implementado
- ✅ Consultas optimizadas
- ✅ Lazy loading en relaciones
- ✅ Transacciones apropiadas

### Testing
- ✅ **Datos de prueba precargados**
- ✅ **Usuarios de diferentes roles**
- ✅ **Catálogo de productos variado**
- ✅ **Escenarios de testing reales**

## Contribución

1. Fork del proyecto
2. Crear rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -am 'Agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

## Licencia

MIT License - ver archivo [LICENSE](LICENSE) para detalles

---

**Desarrollado con Spring Boot 3.5.6 siguiendo buenas prácticas, principios SOLID, arquitectura por capas y datos de prueba realistas para testing completo** 🚀
