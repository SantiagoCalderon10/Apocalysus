package com.example.backendApocalysus.Repositorios;


import com.example.backendApocalysus.Entidades.Categoria;
import com.example.backendApocalysus.Entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
//Verificar si ya existe un producto con el nombre especificado, sin contar el ID especificado
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer idProducto);

    // 🔹 Verificar si ya existe un producto con un nombre (ignorando mayúsculas/minúsculas)
    boolean existsByNombreIgnoreCase(String nombre);

    // 🔹 Buscar producto por nombre exacto (ignorando mayúsculas/minúsculas)
    Optional<Producto> findByNombreIgnoreCase(String nombre);

    // 🔹 Buscar productos cuyo nombre contenga un texto (búsqueda parcial, case-insensitive)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // 🔹 Buscar productos por categoría
    List<Producto> findByCategoria(Categoria categoria);

    // 🔹 Buscar productos con stock disponible (> 0)
    @Query("SELECT p FROM Producto p WHERE p.cantidadDisponible > 0")
    List<Producto> findProductosConStock();


}
