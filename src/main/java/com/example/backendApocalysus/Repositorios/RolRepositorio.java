package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RolRepositorio extends JpaRepository<Rol, Integer> {
}
