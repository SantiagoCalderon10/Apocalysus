package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Dto.CarritoDTO;
import com.example.backendApocalysus.Servicios.CarritoServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
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
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable int idUsuario) {
        try {
            CarritoDTO carrito = carritoServicio.obtenerCarritoPorUsuario(idUsuario);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Agregar producto al carrito
    @PostMapping("/{idUsuario}/agregar")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @PathVariable int idUsuario,
            @RequestParam int idProducto,
            @RequestParam int cantidad
    ) {
        try {
            CarritoDTO carritoActualizado = carritoServicio.agregarProducto(idUsuario, idProducto, cantidad);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Eliminar producto del carrito
    @DeleteMapping("/{idUsuario}/eliminar")
    public ResponseEntity<CarritoDTO> eliminarProducto(
            @PathVariable int idUsuario,
            @RequestParam int idProducto
    ) {
        try {
            CarritoDTO carritoActualizado = carritoServicio.eliminarProducto(idUsuario, idProducto);
            return ResponseEntity.ok(carritoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Vaciar todo el  carrito
    @DeleteMapping("/{idUsuario}/vaciar")
    public ResponseEntity<String> vaciarCarrito(@PathVariable int idUsuario) {
        try {
            carritoServicio.vaciarCarrito(idUsuario);
            return ResponseEntity.ok("Carrito vaciado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al vaciar carrito: " + e.getMessage());
        }
    }




}
