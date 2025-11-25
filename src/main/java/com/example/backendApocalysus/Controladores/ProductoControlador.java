package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Dto.CategoriaDTO;
import com.example.backendApocalysus.Dto.ProductoDTO;
import com.example.backendApocalysus.Entidades.Categoria;
import com.example.backendApocalysus.Servicios.CategoriaServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;


@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {


    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private CategoriaServicio categoriaServicio;


    @Operation(
            summary = "Crear producto (Admin)",
            description = "Crea un nuevo producto en el catálogo. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        try {
            ProductoDTO nuevoProducto = productoServicio.crearProducto(productoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 🔹 Obtener todos los productos


    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene el catálogo completo de productos disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class))))
    })

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosProductos() {
        List<ProductoDTO> productos = productoServicio.obtenerTodosProductos();
        return ResponseEntity.ok(productos);
    }

    // 🔹 Obtener producto por ID
    @Operation(
            summary = "Obtener producto por ID",
            description = "Obtiene información detallada de un producto específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable int id) {
        try {
            ProductoDTO producto = productoServicio.obtenerProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

        // 🔹 Actualizar producto completo
        @Operation(
                summary = "Actualizar producto",
                description = "Modifica por completo un producto existente."
        )
        @SecurityRequirement(name = "Bearer Authentication")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
                @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        })
        @PutMapping("/{id}")
        public ResponseEntity<?> actualizarProducto(
                @PathVariable int id,
                @Valid @RequestBody ProductoDTO productoDTO) {
            try {
                ProductoDTO productoActualizado = productoServicio.actualizarProducto(id, productoDTO);
                return ResponseEntity.ok(productoActualizado);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

    // 🔹 Eliminar producto
    @Operation(
            summary = "Eliminar producto (Admin)",
            description = "Elimina un producto del catálogo. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable int id) {
        try {
            productoServicio.eliminarProducto(id);
            return ResponseEntity.ok("Producto eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 🔹 Actualizar solo el stock (PATCH)
    @Operation(
            summary = "Actualizar stock",
            description = "Modifica únicamente la cantidad de stock del producto."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable int id, @RequestParam int cantidad) {
        try {
            ProductoDTO producto = productoServicio.actualizarStock(id, cantidad);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
