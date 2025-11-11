package com.example.backendApocalysus.Repositorios;


import com.example.backendApocalysus.Entidades.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {


}
