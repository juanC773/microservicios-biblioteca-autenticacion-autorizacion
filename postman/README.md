# Newman – pruebas automáticas de endpoints

Para no probar todo a mano en Postman, puedes ejecutar la colección con **Newman** (CLI de Postman).

## Requisitos

- **Keycloak** en marcha (`docker-compose up -d`) con el realm `biblioteca` configurado (clientes, roles, usuario `librarian1`).
- **Los 4 microservicios** en marcha (`.\run-all.ps1`).
- **Node/npm** (para `npx newman`).

El entorno `local.environment.json` ya lleva el client secret de `circulacion-service` para el realm biblioteca. Si en tu Keycloak el cliente tiene otro secret (p. ej. tras importar el realm), cambia `kc_client_secret` en ese archivo.

## Ejecutar


 desde la carpeta `postman`:

```powershell
.\run-newman.ps1
```

La colección:

1. Pide token de admin (master) y crea/asigna el usuario `newman_user` con ROLE_USER en el realm `biblioteca`.
2. Pide token para `librarian1` y para `newman_user` (con `circulacion-service` + secret).
3. Prueba endpoints públicos (200), sin token (401), con ROLE_USER donde debe 200 o 403, y con ROLE_LIBRARIAN (200/204).
4. Prueba prestar, listar préstamos, devolver.

Si algo falla, revisa que Keycloak tenga el realm y usuarios configurados y que `kc_client_secret` en el entorno sea el correcto.
