package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.Libro;
import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class CatalogoController {
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por id", description = "Público. No requiere autenticación.")
    public Libro obtenerLibro(@PathVariable String id) {
        return catalogoService.obtenerLibro(new LibroId(id));
    }

    @GetMapping("/{id}/disponible")
    @Operation(summary = "Consultar si el libro está disponible", description = "Público. No requiere autenticación.")
    public boolean isLibroDisponible(@PathVariable String id) {
        Libro libro = catalogoService.obtenerLibro(new LibroId(id));
        return libro != null && libro.isDisponible();
    }

    @PutMapping("/{id}/disponibilidad")
    @Operation(summary = "Actualizar disponibilidad del libro", description = "Requiere autenticación. Rol: ROLE_LIBRARIAN. Body: true o false (JSON).")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void actualizarDisponibilidad(@PathVariable String id, @RequestBody boolean disponible) {
        catalogoService.actualizarDisponibilidad(new LibroId(id), disponible);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar libros por criterio", description = "Público. Query param: criterio (ej. título).")
    public List<Libro> buscarLibros(@RequestParam String criterio) {
        return catalogoService.buscarLibros(criterio);
    }
}
