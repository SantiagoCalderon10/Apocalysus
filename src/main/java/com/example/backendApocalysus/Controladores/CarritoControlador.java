package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Dto.CarritoDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Seguridad.UserDetailsImpl;
import com.example.backendApocalysus.Servicios.CarritoServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carrito de Compras", description = "Gestión del carrito de compras del usuario autenticado")
@RestController
@RequestMapping("/api/carrito")
public class CarritoControlador {


    @Autowired
    private CarritoServicio carritoServicio;

    @Autowired
    private ProductoServicio productoServicio;
    @Operation(
            summary = "Obtener carrito",
            description = "Obtiene el carrito de compras del usuario autenticado con todos sus productos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = CarritoDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
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

    @Operation(
            summary = "Agregar producto al carrito",
            description = "Agrega un producto al carrito o incrementa su cantidad si ya existe"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente",
                    content = @Content(schema = @Schema(implementation = CarritoDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })

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

    @Operation(
            summary = "Actualizar cantidad de producto",
            description = "Actualiza la cantidad de un producto específico en el carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CarritoDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
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

    @Operation(
            summary = "Eliminar producto del carrito",
            description = "Elimina un producto específico del carrito de compras"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente",
                    content = @Content(schema = @Schema(implementation = CarritoDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
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
    @Operation(
            summary = "Vaciar carrito",
            description = "Elimina todos los productos del carrito de compras"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito vaciado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error al vaciar el carrito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
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
