package com.example.backendApocalysus.Servicios;


import com.example.backendApocalysus.Dto.ProductoDTO;
import com.example.backendApocalysus.Entidades.Categoria;
import com.example.backendApocalysus.Entidades.Producto;
import com.example.backendApocalysus.Repositorios.CategoriaRepositorio;
import com.example.backendApocalysus.Repositorios.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backendApocalysus.Dto.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    // CREATE con validación de duplicados
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {

        String nombreNormalizado = productoDTO.getNombre().trim().replaceAll("\\s+", " ");


        // 🔹 Validaciones básicas
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }
        if (productoDTO.getCantidadDisponible() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        if ((productoDTO.getIdCategoria() == null) || (productoDTO.getIdCategoria() <= 0)) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        if (productoRepositorio.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + productoDTO.getNombre());
        }

        // 🔹 Buscar categoría
        Categoria categoria = categoriaRepositorio.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoDTO.getIdCategoria()));

        // 🔹 Crear entidad a partir del DTO
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setCantidadDisponible(productoDTO.getCantidadDisponible());
        producto.setImagenUrl(productoDTO.getImagenUrl());
        producto.setCategoria(categoria);

        // 🔹 Guardar y devolver el DTO
            Producto productoGuardado = productoRepositorio.save(producto);
        return convertirAProductoDTO(productoGuardado);
    }

    // READ - Todos los productos
    public List<ProductoDTO> obtenerTodosProductos() {
        return productoRepositorio.findAll().stream()
                .map(this::convertirAProductoDTO)
                .collect(Collectors.toList());
    }



    // READ - Por ID
    public ProductoDTO obtenerProductoPorId(int id) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirAProductoDTO(producto);
    }


    // UPDATE
    public ProductoDTO actualizarProducto(int id, ProductoDTO productoDTO) {

        Producto productoExistente = productoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // 🔹 Validaciones básicas
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }


        // ✅ Validar duplicado (excepto el producto actual)

        String nombreNormalizado = productoDTO.getNombre().trim().replaceAll("\\s+", " ");


        if (productoRepositorio.existsByNombreIgnoreCaseAndIdNot(nombreNormalizado, id)) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + productoDTO.getNombre());
        }

        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        if (productoDTO.getCantidadDisponible() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        if (productoDTO.getIdCategoria() == null || productoDTO.getIdCategoria() <= 0) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        // 🔹 Buscar la categoría
        Categoria categoria = categoriaRepositorio.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoDTO.getIdCategoria()));

        // 🔹 Actualizar los campos
        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setDescripcion(productoDTO.getDescripcion());
        productoExistente.setPrecio(productoDTO.getPrecio());
        productoExistente.setCantidadDisponible(productoDTO.getCantidadDisponible());
        productoExistente.setImagenUrl(productoDTO.getImagenUrl());
        productoExistente.setCategoria(categoria);

        // 🔹 Guardar y devolver
        Producto actualizado = productoRepositorio.save(productoExistente);
        return convertirAProductoDTO(actualizado);
    }




    // DELETE
    public void eliminarProducto(int id) {
        if (!productoRepositorio.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepositorio.deleteById(id);
    }

    // Actualizar stock
    public ProductoDTO actualizarStock(int id, int nuevaCantidad) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        producto.setCantidadDisponible(nuevaCantidad);
        Producto productoActualizado = productoRepositorio.save(producto);
        return convertirAProductoDTO(productoActualizado);
    }

    // Buscar por nombre (devuelve el primer resultado encontrado)
    public ProductoDTO obtenerProductoPorNombre(String nombre) {
       Optional<Producto> productos = productoRepositorio.findByNombreIgnoreCase(nombre);
        if (productos.isEmpty()) {
            throw new RuntimeException("No se encontró ningún producto con nombre que contenga: " + nombre);
        }
        return convertirAProductoDTO(productos.get());
    }




    // 🔹 Método de utilidad para convertir entidad → DTO
    private ProductoDTO convertirAProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCantidadDisponible(producto.getCantidadDisponible());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setIdCategoria(producto.getCategoria().getId());
        dto.setNombreCategoria(producto.getCategoria().getNombre());    
        return dto;
    }


    public long contarProductos() {
        return productoRepositorio.count();
    }
}
