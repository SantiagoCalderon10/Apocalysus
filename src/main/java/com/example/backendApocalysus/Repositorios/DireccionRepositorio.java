package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Direccion;
import com.example.backendApocalysus.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DireccionRepositorio extends JpaRepository<Direccion, Integer> {

    List<Direccion> findByUsuario(Usuario usuario);
}
