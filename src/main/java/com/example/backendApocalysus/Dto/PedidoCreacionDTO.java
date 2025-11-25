package com.example.backendApocalysus.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PedidoCreacionDTO {


    private int idUsuario;

    @NotNull(message = "El ID de la dirección es obligatorio")
    @Min(value = 1, message = "El ID de la dirección debe ser mayor a 0")
    private int idDireccion;

    @NotNull(message = "El ID del método de pago es obligatorio")
    @Min(value = 1, message = "El ID del método de pago debe ser mayor a 0")
    private int idMetodoPago;

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public int getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(int idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
