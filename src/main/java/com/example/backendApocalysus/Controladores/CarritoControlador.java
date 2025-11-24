package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Dto.CarritoDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Seguridad.UserDetailsImpl;
import com.example.backendApocalysus.Servicios.CarritoServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoControlador {


    @Autowired
    private CarritoServicio carritoServicio;

    @Autowired
    private ProductoServicio productoServicio;

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        int idUsuario = SecurityUtils.getUserId();
        try {
            CarritoDTO carrito = carritoServicio.obtenerCarritoPorUsuario(idUsuario);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new CarritoDTO());
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(
            @RequestParam int idProducto,
            @RequestParam int cantidad
    ) {
        int idUsuario = SecurityUtils.getUserId();
        try {
            CarritoDTO carritoActualizado = carritoServicio.agregarProducto(idUsuario, idProducto, cantidad);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new CarritoDTO());
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarCantidad(
            @RequestParam int idProducto,
            @RequestParam int cantidad
    ) {
        int idUsuario = SecurityUtils.getUserId();
        try {
            CarritoDTO carritoActualizado = carritoServicio.actualizarCantidad(idUsuario, idProducto, cantidad);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new CarritoDTO());
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarProducto(@RequestParam int idProducto) {
        int idUsuario = SecurityUtils.getUserId();
        try {
            CarritoDTO carritoActualizado = carritoServicio.eliminarProducto(idUsuario, idProducto);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new CarritoDTO());
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciarCarrito() {
        int idUsuario = SecurityUtils.getUserId();
        try {
            carritoServicio.vaciarCarrito(idUsuario);
            return ResponseEntity.ok("Carrito vaciado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al vaciar: " + e.getMessage());
        }
    }
}
