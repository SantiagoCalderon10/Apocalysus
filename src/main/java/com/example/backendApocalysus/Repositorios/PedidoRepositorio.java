package com.example.backendApocalysus.Repositorios;

import com.example.backendApocalysus.Entidades.Pedido;
import com.example.backendApocalysus.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario(Usuario usuario);

}
