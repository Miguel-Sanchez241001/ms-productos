# ms-productos

Microservicio de gestión del catálogo de productos. Permite registrar, listar, buscar, actualizar y eliminar productos mediante una API REST.

## Tecnologías utilizadas

| Tecnología       | Versión  |
|------------------|----------|
| Java             | 21       |
| Spring Boot      | 3.5.0    |
| Spring Web       | —        |
| Spring Data JPA  | —        |
| PostgreSQL Driver| —        |
| Validation (JSR-380) | —    |
| Lombok           | —        |
| Maven            | 3.9+     |
| Neon (PostgreSQL en la nube) | — |
| Docker           | —        |
| Render           | —        |

## Entidad: Producto

| Campo         | Tipo         | Descripción                          |
|---------------|--------------|--------------------------------------|
| id            | Long         | Identificador único (auto-generado)  |
| nombre        | String       | Nombre del producto                  |
| descripcion   | String       | Descripción breve                    |
| precio        | BigDecimal   | Precio unitario                      |
| stock         | Integer      | Cantidad disponible                  |
| estado        | Boolean      | true = activo, false = inactivo      |
| fechaCreacion | LocalDateTime| Fecha de creación (auto-asignada)    |

## Endpoints disponibles

### POST /api/productos — Crear producto
```http
POST /api/productos
Content-Type: application/json

{
  "nombre": "Laptop Lenovo",
  "descripcion": "Laptop para desarrollo de software",
  "precio": 3500.00,
  "stock": 10,
  "estado": true
}
```

### GET /api/productos — Listar todos los productos
```http
GET /api/productos
```

### GET /api/productos/{id} — Buscar por ID
```http
GET /api/productos/1
```

### PUT /api/productos/{id} — Actualizar producto
```http
PUT /api/productos/1
Content-Type: application/json

{
  "nombre": "Laptop Lenovo IdeaPad",
  "descripcion": "Laptop actualizada",
  "precio": 3200.00,
  "stock": 8,
  "estado": true
}
```

### DELETE /api/productos/{id} — Eliminar (lógico)
```http
DELETE /api/productos/1
```
> La eliminación es lógica: cambia el campo `estado` a `false`.

## Respuesta de error

```json
{
  "mensaje": "Producto no encontrado",
  "detalle": "No existe un producto con el ID 10",
  "fecha": "2026-05-09T10:30:00"
}
```

## Variables de entorno necesarias

| Variable      | Descripción                                 | Ejemplo                                                   |
|---------------|---------------------------------------------|-----------------------------------------------------------|
| `DB_URL`      | JDBC URL de conexión a PostgreSQL en Neon   | `jdbc:postgresql://host/neondb?sslmode=require`           |
| `DB_USERNAME` | Usuario de la base de datos                 | `neondb_owner`                                            |
| `DB_PASSWORD` | Contraseña de la base de datos              | `tu_password`                                             |
| `PORT`        | Puerto del servidor (default: 8080)         | `8080`                                                    |

## Ejecución en local

### 1. Clonar el repositorio
```bash
git clone https://github.com/Miguel-Sanchez241001/ms-productos.git
cd ms-productos
```

### 2. Configurar variables de entorno

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require"
$env:DB_USERNAME="neondb_owner"
$env:DB_PASSWORD="tu_password"
$env:PORT="8080"
```

**Linux / macOS (bash):**
```bash
export DB_URL="jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require"
export DB_USERNAME="neondb_owner"
export DB_PASSWORD="tu_password"
export PORT=8080
```

### 3. Compilar y ejecutar
```bash
mvn clean package -DskipTests
java -jar target/ms-productos-0.0.1-SNAPSHOT.jar
```

O directamente:
```bash
mvn spring-boot:run
```

El servicio estará disponible en: `http://localhost:8080/api/productos`

### 4. Ejecutar con Docker (local)
```bash
docker build -t ms-productos .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require" \
  -e DB_USERNAME="neondb_owner" \
  -e DB_PASSWORD="tu_password" \
  ms-productos
```

## Despliegue en Render

### Prerrequisitos
- Cuenta en [Render](https://render.com)
- Repositorio en GitHub con el código de este proyecto
- Base de datos activa en [Neon](https://neon.tech)

### Pasos de despliegue

1. **Subir el código a GitHub:**
   ```bash
   git init
   git add .
   git commit -m "feat: ms-productos inicial"
   git remote add origin https://github.com/Miguel-Sanchez241001/ms-productos.git
   git push -u origin main
   ```

2. **Crear servicio en Render:**
   - Ir a [render.com](https://render.com) → **New** → **Web Service**
   - Conectar tu cuenta de GitHub y seleccionar el repositorio `ms-productos`
   - Configurar:
     - **Name:** `ms-productos`
     - **Language:** `Docker`
     - **Branch:** `main`
     - **Dockerfile Path:** `./Dockerfile`

3. **Configurar variables de entorno en Render:**
   
   En la sección **Environment** del servicio, agregar:

   | Key           | Value                                                                                  |
   |---------------|----------------------------------------------------------------------------------------|
   | `DB_URL`      | `jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require` |
   | `DB_USERNAME` | `neondb_owner`                                                                         |
   | `DB_PASSWORD` | *(tu contraseña de Neon)*                                                              |
   | `PORT`        | `8080`                                                                                 |

4. **Iniciar el despliegue:**
   - Hacer clic en **Create Web Service**
   - Esperar a que el build de Docker finalice (puede tardar 3-5 minutos la primera vez)

5. **Verificar el despliegue:**
   ```bash
   curl https://ms-productos.onrender.com/api/productos
   ```

> **Nota:** En el plan gratuito de Render, el servicio entra en "sleep" tras 15 minutos de inactividad. La primera petición puede tardar ~30 segundos en despertar.

### Uso con render.yaml (Blueprint)

Alternativamente, usa el archivo `render.yaml` incluido en el proyecto:
- Ir a Render → **New** → **Blueprint**
- Conectar el repositorio
- Render detectará el `render.yaml` y configurará el servicio automáticamente
- Solo tendrás que ingresar las variables marcadas como `sync: false` (las credenciales)

## URL del servicio desplegado

```
https://ms-productos.onrender.com/api/productos
```
*(URL de ejemplo — reemplazar con la URL real de Render tras el despliegue)*

## Configuración de Neon (base de datos)

La tabla `productos` se crea automáticamente al iniciar el servicio gracias a `spring.jpa.hibernate.ddl-auto=update`.

Para verificar en Neon:
1. Ir a [neon.tech](https://neon.tech) → tu proyecto
2. Abrir el **SQL Editor**
3. Ejecutar: `SELECT * FROM productos;`

## Estructura del proyecto

```
ms-productos/
├── src/main/java/com/examen/productos/
│   ├── MsProductosApplication.java
│   ├── controller/
│   │   └── ProductoController.java
│   ├── service/
│   │   └── ProductoService.java
│   ├── repository/
│   │   └── ProductoRepository.java
│   ├── entity/
│   │   └── Producto.java
│   ├── dto/
│   │   ├── ProductoRequestDTO.java
│   │   └── ProductoResponseDTO.java
│   └── exception/
│       ├── ProductoNotFoundException.java
│       ├── GlobalExceptionHandler.java
│       └── ErrorResponse.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── render.yaml
├── .env.example
└── pom.xml
```
