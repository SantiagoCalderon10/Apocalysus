package com.example.backendApocalysus.Controladores;
import com.example.backendApocalysus.Dto.*;
import com.example.backendApocalysus.Entidades.*;
import com.example.backendApocalysus.Servicios.CategoriaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;
@Tag(name = "Categorías", description = "Gestión de categorías de productos")

@RestController
@RequestMapping("/api/categorias")
public class CategoriaControlador {
    @Autowired
    private CategoriaServicio categoriaServicio;



    @Operation(
            summary = "Crear categoría (Admin)",
            description = "Crea una nueva categoría de productos. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @PostMapping
    public CategoriaDTO crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        return categoriaServicio.crearCategoria(categoriaDTO);
    }

    // READ - Obtener todas
    @Operation(
            summary = "Listar categorías",
            description = "Obtiene todas las categorías disponibles en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoriaDTO.class))))
    })
    @GetMapping
    public List<CategoriaDTO> obtenerCategorias() {
        return categoriaServicio.obtenerCategorias();
    }

    @Operation(
            summary = "Obtener categoría por ID",
            description = "Obtiene información detallada de una categoría específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    // READ - Obtener por ID
    @GetMapping("/{id}")
    public CategoriaDTO obtenerCategoria(@PathVariable int id) {
        return categoriaServicio.obtenerCategoriaPorId(id);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar categoría (Admin)",
            description = "Actualiza una categoría existente. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public CategoriaDTO actualizarCategoria(
            @PathVariable int id,
            @RequestBody CategoriaDTO categoriaDTO
    ) {
        return categoriaServicio.actualizarCategoria(id, categoriaDTO);
    }

    // DELETE
    @Operation(
            summary = "Eliminar categoría (Admin)",
            description = "Elimina una categoría del sistema. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    public String eliminarCategoria(@PathVariable int id) {
        categoriaServicio.eliminarCategoria(id);
        return "Categoría eliminada correctamente";
    }
}
