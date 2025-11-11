package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DetalleCarritoRepositorio extends JpaRepository<DetalleCarrito, Integer> {


    // Buscar detalles de un carrito específico
    List<DetalleCarrito> findByCarritoId(int carritoId);

    // Buscar un detalle específico por carrito y producto
    Optional<DetalleCarrito> findByCarritoIdAndProductoId(int carritoId, int productoId);

    // Eliminar todos los detalles de un carrito
    void deleteByCarritoId(int carritoId);

    // Contar items en el carrito de un usuario
    @Query("SELECT SUM(dc.cantidad) FROM DetalleCarrito dc WHERE dc.carrito.usuario.idUsuario = :usuarioId AND dc.carrito.activo = true")
    Optional<Integer> countItemsByUsuarioId(int usuarioId);
}
