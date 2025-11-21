package com.example.backendApocalysus.Controladores;
import com.example.backendApocalysus.Dto.*;
import com.example.backendApocalysus.Entidades.*;
import com.example.backendApocalysus.Servicios.CategoriaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaControlador {
    @Autowired
    private CategoriaServicio categoriaServicio;

    // CREATE
    @PostMapping
    public CategoriaDTO crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        return categoriaServicio.crearCategoria(categoriaDTO);
    }

    // READ - Obtener todas
    @GetMapping
    public List<CategoriaDTO> obtenerCategorias() {
        return categoriaServicio.obtenerCategorias();
    }

    // READ - Obtener por ID
    @GetMapping("/{id}")
    public CategoriaDTO obtenerCategoria(@PathVariable int id) {
        return categoriaServicio.obtenerCategoriaPorId(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public CategoriaDTO actualizarCategoria(
            @PathVariable int id,
            @RequestBody CategoriaDTO categoriaDTO
    ) {
        return categoriaServicio.actualizarCategoria(id, categoriaDTO);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String eliminarCategoria(@PathVariable int id) {
        categoriaServicio.eliminarCategoria(id);
        return "Categoría eliminada correctamente";
    }
}
