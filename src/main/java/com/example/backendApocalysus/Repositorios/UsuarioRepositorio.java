package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String email);
}
