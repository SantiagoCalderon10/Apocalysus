package com.example.backendApocalysus.Repositorios;


import com.example.backendApocalysus.Entidades.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepositorio extends JpaRepository<MetodoPago, Integer> {
}
