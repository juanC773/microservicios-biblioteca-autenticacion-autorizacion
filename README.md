# Microservicios Biblioteca – Autenticación y Autorización

Proyecto de **APIs RESTful seguras** para un sistema de biblioteca basado en microservicios. Incluye autenticación con **Keycloak** (JWT) y autorización por roles en los endpoints.

## De qué trata el proyecto

- **Cuatro microservicios** Spring Boot (Java 17): Usuarios, Catálogo, Circulación y Notificación.
- **Keycloak** como servidor de identidad: emite JWT y gestiona roles (`ROLE_LIBRARIAN`, `ROLE_USER`).
- Los endpoints clave exigen **token Bearer** y **roles** según la operación (solo bibliotecario puede prestar/devolver; usuario puede ver préstamos y su perfil, etc.).
- **Swagger/OpenAPI** en cada servicio para probar los endpoints con autenticación.

| Microservicio | Puerto | Descripción |
|---------------|--------|-------------|
| Usuarios      | 8081   | Usuarios y cambio de email |
| Catálogo      | 8082   | Libros, disponibilidad, búsqueda |
| Circulación   | 8083   | Préstamos y devoluciones |
| Notificación  | 8084   | Envío de notificaciones |

La parte de **autenticación y autorización** (lo central de la entrega) se explica en detalle en **[docs/autenticacion-autorizacion.md](docs/autenticacion-autorizacion.md)**.

## Cómo ejecutar todo

### Requisitos

- Java 17  
- Maven (o usar el `mvnw` incluido en cada microservicio)  
- Docker (para Keycloak)

### 1. Levantar Keycloak

En la raíz del proyecto:

```powershell
docker-compose up -d
```

Esperar unos 30 segundos y abrir **http://localhost:8080/admin** (usuario `admin`, contraseña `admin`).  
Configuración del realm, clientes, roles y usuarios: ver **[docs/guia-keycloak-pasos.md](docs/guia-keycloak-pasos.md)**.

### 2. Levantar los cuatro microservicios

En la raíz del proyecto:

```powershell
.\run-all.ps1
```

Se abren cuatro ventanas (una por servicio). Esperar a que cada uno arranque (mensaje tipo “Started …Application”).  
Puertos: **8081**, **8082**, **8083**, **8084**.

### 3. Parar los servicios

Cerrar las ventanas de PowerShell de cada microservicio.

## Swagger (documentación y pruebas)

Con los servicios en marcha:

| Servicio    | URL Swagger UI |
|------------|-----------------|
| Usuarios   | http://localhost:8081/swagger-ui.html |
| Catálogo   | http://localhost:8082/swagger-ui.html |
| Circulación| http://localhost:8083/swagger-ui.html |
| Notificación | http://localhost:8084/swagger-ui.html |

En cada Swagger: **Authorize** → pegar el JWT (token obtenido de Keycloak) → probar los endpoints.

**Pantallazos de Swagger** usados en la entrega: **[docs/swagger pantallazos](docs/swagger%20pantallazos)**.

## Probar endpoints con Newman (Postman CLI)

En la carpeta **[postman/](postman/)** hay una colección y un entorno para ejecutar todas las pruebas de endpoints desde la terminal (sin abrir Postman). Requisitos: Keycloak y los cuatro microservicios en marcha. Desde la raíz del proyecto:

```powershell
npx newman run postman/biblioteca.newman.collection.json -e postman/local.environment.json -r cli
```

Detalles y alternativas en **[postman/README.md](postman/README.md)**.

## Entregables de la tarea

- **Código fuente**: este repositorio (los cuatro microservicios y la raíz).
- **Configuración Keycloak exportada**: **[docs/realm-export.json](docs/realm-export.json)** (export del realm `biblioteca`).
- **Documentación Swagger**: pantallazos en **[docs/swagger pantallazos](docs/swagger%20pantallazos)**.
- **Autenticación y autorización**: explicación en **[docs/autenticacion-autorizacion.md](docs/autenticacion-autorizacion.md)**.

## Estructura del repositorio

```
biblioteca-seguridad/
├── microservicios-usuarios-biblioteca/
├── microservicios-catalogo-biblioteca/
├── microservicios-circulacion-biblioteca/
├── microservicios-notificacion-bilbioteca/
├── docs/
│   ├── autenticacion-autorizacion.md   # Explicación auth/authz (valor de la entrega)
│   ├── guia-keycloak-pasos.md         # Pasos en Keycloak
│   ├── como-exportar-keycloak.md      # Cómo exportar el realm
│   ├── realm-export.json              # Export del realm (config Keycloak)
│   └── swagger pantallazos/           # Capturas de Swagger UI
├── postman/                           # Colección Newman + entorno (pruebas CLI)
│   ├── biblioteca.newman.collection.json
│   ├── local.environment.json
│   └── README.md
├── docker-compose.yml                 # Keycloak
├── run-all.ps1
└── README.md
```
