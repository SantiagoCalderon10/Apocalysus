package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DetallePedidoRepositorio extends JpaRepository<DetallePedido, Integer> {
}
