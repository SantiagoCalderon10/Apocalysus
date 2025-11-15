package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DireccionRepositorio extends JpaRepository<Direccion, Long> {


}
