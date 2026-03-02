# Autenticación y autorización en los microservicios de biblioteca

Este documento describe **qué se implementó** en materia de autenticación y autorización para la entrega: uso de Keycloak, JWT, roles y protección de endpoints.

---

## 1. Objetivo

- **Autenticación**: que los endpoints sensibles exijan un **JWT** emitido por Keycloak (realm `biblioteca`).
- **Autorización**: que el acceso dependa del **rol** del usuario (`ROLE_LIBRARIAN`, `ROLE_USER`) mediante anotaciones `@PreAuthorize`.
- **Propagación de identidad**: cuando Circulación llama a Catálogo o Notificación, se reenvía el mismo token para no recibir 401.

---

## 2. Keycloak como IdP

- **Keycloak** actúa como servidor de identidad (IdP): gestiona usuarios, contraseñas y emite **tokens JWT**.
- Se usa un **realm** llamado `biblioteca`.
- **Clientes** (uno por microservicio): `usuarios-service`, `catalogo-service`, `circulacion-service`, `notificacion-service`, tipo confidential, con Direct access grants para obtener token con usuario/contraseña.
- **Roles del realm**: `ROLE_LIBRARIAN` (bibliotecario) y `ROLE_USER` (usuario final). Se asignan a usuarios en Keycloak.
- La **configuración exportada** del realm está en **[docs/realm-export.json](realm-export.json)**.

---

## 3. Cómo se valida el JWT en cada microservicio

En los cuatro servicios se configuró **Spring Security** como **OAuth2 Resource Server**:

- **issuer-uri**: `http://localhost:8080/realms/biblioteca` → Spring comprueba que el `iss` del JWT coincida.
- **jwk-set-uri**: se usan las claves públicas del realm para **verificar la firma** del token.

Si el token es inválido o ha caducado, la petición recibe **401 Unauthorized**.

---

## 4. Uso de los roles (autorización)

Los roles en Keycloak están en el claim **`realm_access.roles`** del JWT. En el proyecto se usa un **converter** (`KeycloakRealmRoleConverter`) que:

- Lee esos roles del token.
- Los convierte en `GrantedAuthority` con el prefijo `ROLE_` si no lo llevan.
- Así, en los controladores se puede usar `hasRole('ROLE_LIBRARIAN')` y `hasAnyRole('ROLE_USER','ROLE_LIBRARIAN')`.

La **autorización** se aplica con la anotación **`@PreAuthorize`** en cada endpoint.

---

## 5. Qué está protegido y con qué rol

Resumen por servicio:

| Servicio    | Endpoint                         | Acceso / rol |
|------------|-----------------------------------|--------------|
| **Usuarios** | GET `/usuarios/{id}`             | `ROLE_USER` o `ROLE_LIBRARIAN` |
|             | PUT `/usuarios/{id}/email`       | `ROLE_USER` o `ROLE_LIBRARIAN` |
| **Catálogo** | GET `/libros/**`                 | Público (sin token) |
|             | PUT `/libros/{id}/disponibilidad` | Solo `ROLE_LIBRARIAN` |
| **Circulación** | GET `/circulacion/public/status` | Público |
|             | POST `/circulacion/prestar`      | Solo `ROLE_LIBRARIAN` |
|             | POST `/circulacion/devolver`     | Solo `ROLE_LIBRARIAN` |
|             | GET `/circulacion/prestamos`     | `ROLE_LIBRARIAN` o `ROLE_USER` |
| **Notificación** | POST `/notificar`             | Solo `ROLE_LIBRARIAN` |

Sin token → **401**. Con token pero sin el rol requerido → **403 Forbidden**.

---

## 6. Propagación del token (Circulación → Catálogo y Notificación)

El servicio de **Circulación** llama por HTTP (RestTemplate) a:

- **Catálogo**: consultar disponibilidad y actualizar disponibilidad al prestar/devolver.
- **Notificación**: enviar notificación al usuario.

Esos endpoints están protegidos con JWT. Para que las llamadas no fallen con 401, se añadió un **interceptor** en el `RestTemplate` de Circulación que:

1. Obtiene la autenticación actual del `SecurityContextHolder`.
2. Si el principal es un `Jwt`, toma su valor (`getTokenValue()`).
3. Añade el header `Authorization: Bearer <token>` a cada petición saliente.

Así, el mismo token del usuario que llamó a Circulación se reenvía a Catálogo y Notificación.

---

## 7. Resumen técnico

- **Autenticación**: JWT de Keycloak (realm `biblioteca`), validado en cada microservicio con issuer + JWK.
- **Autorización**: roles `ROLE_LIBRARIAN` y `ROLE_USER` extraídos del JWT y usados con `@PreAuthorize`.
- **Rutas públicas**: Swagger UI, actuator, en Catálogo los GET de libros, en Circulación `/circulacion/public/**`; el resto exige token y rol.
- **Documentación**: Swagger/OpenAPI en cada servicio con esquema Bearer JWT; pantallazos en **[docs/swagger pantallazos](swagger%20pantallazos)**.

La configuración exportada de Keycloak para este realm está en **[docs/realm-export.json](realm-export.json)**.
