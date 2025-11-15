package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Carrito;
import com.example.backendApocalysus.Entidades.DetalleCarrito;
import com.example.backendApocalysus.Entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DetalleCarritoRepositorio extends JpaRepository<DetalleCarrito, Integer> {


    Optional<DetalleCarrito> findByCarritoAndProducto(Carrito carrito, Producto producto);
    List<DetalleCarrito> findByCarrito_IdCarrito(int idCarrito);

}
