package com.example.backendApocalysus.Entidades;


import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String calle;
    private String ciudad;
    private String departamento;
    private String pais;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

}
