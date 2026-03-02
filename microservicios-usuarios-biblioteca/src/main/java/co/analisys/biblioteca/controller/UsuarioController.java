package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.Email;
import co.analisys.biblioteca.model.Usuario;
import co.analisys.biblioteca.model.UsuarioId;
import co.analisys.biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id", description = "Requiere autenticación. Roles: ROLE_USER o ROLE_LIBRARIAN.")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_LIBRARIAN')")
    public Usuario obtenerUsuario(@PathVariable String id) {
        return usuarioService.obtenerUsuario(new UsuarioId(id));
    }

    @PutMapping("/{id}/email")
    @Operation(summary = "Cambiar email del usuario", description = "Requiere autenticación. Roles: ROLE_USER o ROLE_LIBRARIAN. Body: texto plano con el nuevo email.")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_LIBRARIAN')")
    public void cambiarEmail(@PathVariable String id, @RequestBody String nuevoEmail) {
        usuarioService.cambiarEmailUsuario(new UsuarioId(id), new Email(nuevoEmail));
    }
}
