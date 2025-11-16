package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Dto.CarritoDTO;
import com.example.backendApocalysus.Servicios.CarritoServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoControlador {


    @Autowired
    private CarritoServicio carritoServicio;

    @Autowired
    private ProductoServicio productoServicio;


    // ✅ Obtener el carrito activo del usuario
    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> obtenerCarrito(@PathVariable @Min(1) int idUsuario) {
        try {
            CarritoDTO carrito = carritoServicio.obtenerCarritoPorUsuario(idUsuario);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CarritoDTO()); // DTO vacío
        }
    }

    // ✅ Agregar producto al carrito
    @PostMapping("/{idUsuario}/agregar")
    public ResponseEntity<?> agregarProducto(
            @PathVariable @Min(1) int idUsuario,
            @RequestParam @Min(1) int idProducto,
            @RequestParam @Min(1) @Max(100) int cantidad
    ) {
        try {
            CarritoDTO carritoActualizado = carritoServicio.agregarProducto(idUsuario, idProducto, cantidad);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CarritoDTO()); // DTO vacío
        }
    }

    // ✅ Actualizar cantidad de producto en carrito
    @PutMapping("/{idUsuario}/actualizar")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable @Min(1) int idUsuario,
            @RequestParam @Min(1) int idProducto,
            @RequestParam @Min(0) @Max(100) int cantidad
    ) {
        try {
            CarritoDTO carritoActualizado = carritoServicio.actualizarCantidad(idUsuario, idProducto, cantidad);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CarritoDTO()); // DTO vacío
        }
    }

    // ✅ Eliminar producto del carrito
    @DeleteMapping("/{idUsuario}/eliminar")
    public ResponseEntity<?> eliminarProducto(
            @PathVariable @Min(1) int idUsuario,
            @RequestParam @Min(1) int idProducto
    ) {
        try {
            CarritoDTO carritoActualizado = carritoServicio.eliminarProducto(idUsuario, idProducto);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new CarritoDTO()); // DTO vacío
        }
    }

    // ✅ Vaciar todo el carrito
    @DeleteMapping("/{idUsuario}/vaciar")
    public ResponseEntity<?> vaciarCarrito(@PathVariable @Min(1) int idUsuario) {
        try {
            carritoServicio.vaciarCarrito(idUsuario);
            return ResponseEntity.ok("Carrito vaciado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al vaciar carrito: " + e.getMessage());
        }
    }




}
