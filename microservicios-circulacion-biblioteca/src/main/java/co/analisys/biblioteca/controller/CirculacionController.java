package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.model.Prestamo;
import co.analisys.biblioteca.model.PrestamoId;
import co.analisys.biblioteca.model.UsuarioId;
import co.analisys.biblioteca.service.CirculacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/circulacion")
public class CirculacionController {
    @Autowired
    private CirculacionService circulacionService;

    @PostMapping("/prestar")
    @Operation(summary = "Registrar préstamo", description = "Requiere autenticación. Rol: ROLE_LIBRARIAN. Params: usuarioId, libroId.")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void prestarLibro(@RequestParam String usuarioId, @RequestParam String libroId) {
        circulacionService.prestarLibro(new UsuarioId(usuarioId), new LibroId(libroId));
    }

    @PostMapping("/devolver")
    @Operation(summary = "Registrar devolución", description = "Requiere autenticación. Rol: ROLE_LIBRARIAN. Param: prestamoId.")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void devolverLibro(@RequestParam String prestamoId) {
        circulacionService.devolverLibro(new PrestamoId(prestamoId));
    }

    @GetMapping("/prestamos")
    @Operation(summary = "Listar todos los préstamos", description = "Requiere autenticación. Roles: ROLE_LIBRARIAN o ROLE_USER.")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_USER')")
    public List<Prestamo> obtenerTodosPrestamos() {
        return circulacionService.obtenerTodosPrestamos();
    }

    @GetMapping("/public/status")
    @Operation(summary = "Estado del servicio", description = "Público. No requiere autenticación.")
    public String getPublicStatus() {
        return "El servicio de circulación está funcionando correctamente";
    }
}
