package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {
}
