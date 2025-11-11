package com.example.backendApocalysus.Repositorios;


import com.example.backendApocalysus.Entidades.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {

    // Buscar carrito activo de un usuario
    Optional<Carrito> findByUsuarioIdAndActivoTrue(int usuarioId);

    // Buscar todos los carritos de un usuario
    List<Carrito> findByUsuarioId(int usuarioId);
}
