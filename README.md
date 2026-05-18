# ms-productos

API REST para gestión del catálogo de productos. Construida con Spring Boot 3.5 y desplegada en Render con base de datos PostgreSQL en Neon.

🌐 **Producción:** https://ms-productos-id0q.onrender.com

---

## Stack tecnológico

| Capa              | Tecnología                          |
|-------------------|-------------------------------------|
| Lenguaje          | Java 21                             |
| Framework         | Spring Boot 3.5.0                   |
| Persistencia      | Spring Data JPA + Hibernate 6.6     |
| Base de datos     | PostgreSQL (Neon — serverless)      |
| Validaciones      | Jakarta Bean Validation (JSR-380)   |
| Build             | Maven 3.9                           |
| Contenedor        | Docker (multi-stage build)          |
| Despliegue        | Render (Docker runtime)             |

---

## Modelo de datos

| Campo         | Tipo          | Restricción                            |
|---------------|---------------|----------------------------------------|
| id            | Long          | PK, autoincremental                    |
| nombre        | String        | Obligatorio, no vacío                  |
| descripcion   | String        | Opcional                               |
| precio        | BigDecimal    | Obligatorio, mayor que 0               |
| stock         | Integer       | Obligatorio, mayor o igual a 0         |
| estado        | Boolean       | `true` = activo · `false` = inactivo   |
| fechaCreacion | LocalDateTime | Asignada automáticamente en `@PrePersist` |

> La eliminación es **lógica**: el `DELETE` cambia `estado → false`. El registro permanece en la BD.

---

## Endpoints

| Método   | Ruta                    | Descripción                  | Respuesta        |
|----------|-------------------------|------------------------------|------------------|
| `POST`   | `/api/productos`        | Crear producto               | `201 Created`    |
| `GET`    | `/api/productos`        | Listar todos                 | `200 OK`         |
| `GET`    | `/api/productos/{id}`   | Buscar por ID                | `200` / `404`    |
| `PUT`    | `/api/productos/{id}`   | Actualizar todos los campos  | `200` / `404`    |
| `DELETE` | `/api/productos/{id}`   | Desactivar (lógico)          | `204 No Content` |

### POST /api/productos
```json
{
  "nombre": "Laptop Lenovo",
  "descripcion": "Laptop para desarrollo de software",
  "precio": 3500.00,
  "stock": 10
}
```

### PUT /api/productos/{id}
```json
{
  "nombre": "Laptop Lenovo IdeaPad",
  "descripcion": "Laptop actualizada",
  "precio": 3200.00,
  "stock": 8,
  "estado": true
}
```

### Respuesta exitosa (ejemplo POST)
```json
{
  "id": 1,
  "nombre": "Laptop Lenovo",
  "descripcion": "Laptop para desarrollo de software",
  "precio": 3500.00,
  "stock": 10,
  "estado": true,
  "fechaCreacion": "2026-05-17T15:42:29.832"
}
```

### Respuesta de error
```json
{
  "mensaje": "Producto no encontrado",
  "detalle": "No existe un producto con el ID 99",
  "fecha": "2026-05-17T15:42:46.980"
}
```

---

## Ejecución local

### Prerrequisitos
- Java 21
- Maven 3.9+

### 1. Clonar
```bash
git clone https://github.com/TU_USUARIO/ms-productos.git
cd ms-productos
```

### 2. Configurar variables de entorno

**Windows (PowerShell)**
```powershell
$env:DB_URL      = "jdbc:postgresql://HOST/neondb?sslmode=require"
$env:DB_USERNAME = "tu_usuario"
$env:DB_PASSWORD = "tu_password"
$env:PORT        = "8080"
```

**Linux / macOS**
```bash
export DB_URL="jdbc:postgresql://HOST/neondb?sslmode=require"
export DB_USERNAME="tu_usuario"
export DB_PASSWORD="tu_password"
export PORT=8080
```

### 3. Ejecutar
```bash
mvn spring-boot:run
# API disponible en http://localhost:8080/api/productos
```

### 4. Docker (local)
```bash
docker build -t ms-productos .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://HOST/neondb?sslmode=require" \
  -e DB_USERNAME="tu_usuario" \
  -e DB_PASSWORD="tu_password" \
  ms-productos
```

---

## Despliegue en Render

### Variables de entorno requeridas

| Variable      | Descripción                      |
|---------------|----------------------------------|
| `DB_URL`      | `jdbc:postgresql://HOST/neondb?sslmode=require` |
| `DB_USERNAME` | Usuario de Neon                  |
| `DB_PASSWORD` | Contraseña de Neon               |
| `PORT`        | `8080`                           |

### Pasos
1. Ir a [render.com](https://render.com) → **New** → **Web Service**
2. Conectar el repo `ms-productos` desde GitHub
3. Seleccionar **Language: Docker**
4. Agregar las 4 variables de entorno
5. Clic en **Create Web Service** — build tarda ~5 min

> ⚠ En el plan gratuito, el servicio duerme tras 15 min de inactividad. La primera petición puede tardar ~30 s.

---

## Base de datos — Neon

La tabla `productos` se crea automáticamente con `ddl-auto=update`.

Para verificar:
```sql
-- En el SQL Editor de Neon
SELECT * FROM productos;
```

---

## Estructura del proyecto

```
ms-productos/
├── src/main/java/com/examen/productos/
│   ├── controller/   ProductoController.java
│   ├── service/      ProductoService.java
│   ├── repository/   ProductoRepository.java
│   ├── entity/       Producto.java
│   ├── dto/          ProductoRequestDTO.java · ProductoResponseDTO.java
│   └── exception/    GlobalExceptionHandler.java · ProductoNotFoundException.java · ErrorResponse.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── render.yaml
└── pom.xml
```
