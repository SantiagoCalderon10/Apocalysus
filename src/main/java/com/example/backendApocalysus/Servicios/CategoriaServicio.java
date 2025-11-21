package com.example.backendApocalysus.Servicios;

import com.example.backendApocalysus.Controladores.UsuarioControlador;
import com.example.backendApocalysus.Dto.CategoriaDTO;
import com.example.backendApocalysus.Dto.ProductoDTO;
import com.example.backendApocalysus.Entidades.Categoria;
import com.example.backendApocalysus.Entidades.Producto;
import com.example.backendApocalysus.Repositorios.CategoriaRepositorio;
import com.example.backendApocalysus.Repositorios.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaServicio {
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    // 🔄 Convertir entidad → DTO
    private CategoriaDTO convertirADTO(Categoria categoria) {
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

    // 🔄 Convertir DTO → entidad
    private Categoria convertirAEntidad(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        return categoria;
    }

    // ✅ Crear categoría
    public CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO) {
        Categoria categoria = convertirAEntidad(categoriaDTO);

        Categoria guardada = categoriaRepositorio.save(categoria);
        return convertirADTO(guardada);
    }

    // ✅ Obtener todas las categorías
    public List<CategoriaDTO> obtenerCategorias() {
        return categoriaRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ Buscar categoría por ID
    public CategoriaDTO obtenerCategoriaPorId(int id) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return convertirADTO(categoria);
    }

    // ✅ Actualizar categoría
    public CategoriaDTO actualizarCategoria(int id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());

        Categoria actualizada = categoriaRepositorio.save(categoria);
        return convertirADTO(actualizada);
    }

    // ✅ Eliminar categoría
    public void eliminarCategoria(int id) {
        if (!categoriaRepositorio.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }
        categoriaRepositorio.deleteById(id);
    }

}
