package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {
}
