package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DetalleCarritoRepositorio extends JpaRepository<DetalleCarrito, Integer> {



}
