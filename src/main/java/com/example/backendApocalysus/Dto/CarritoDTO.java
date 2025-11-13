package com.example.backendApocalysus.Dto;


import java.time.LocalDateTime;
import java.util.List;

public class CarritoDTO {

    private int idCarrito;

    private int idUsuario;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private boolean activo;

    private Double total;

    private List<CarritoItemDTO> items;


    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public List<CarritoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemDTO> items) {
        this.items = items;
    }

 public int getIdUsuario() {
        return idUsuario;
 }

 public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
 }

 public Double getTotal() {
        return total;
 }

 public void setTotal(Double total) {
        this.total = total;
 }
}
