package com.example.backendApocalysus.Repositorios;


import com.example.backendApocalysus.Entidades.Carrito;
import com.example.backendApocalysus.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByUsuarioAndActivoTrue(Usuario usuario);
    List<Carrito> findAllByUsuarioAndActivoTrue(Usuario usuario);


}

