package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Dto.CategoriaDTO;
import com.example.backendApocalysus.Dto.ProductoDTO;
import com.example.backendApocalysus.Entidades.Categoria;
import com.example.backendApocalysus.Servicios.CategoriaServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {


    @Autowired
    private ProductoServicio productoServicio;
    @Autowired
    private CategoriaServicio categoriaServicio;

    // 🔹 Crear producto
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
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosProductos() {
        List<ProductoDTO> productos = productoServicio.obtenerTodosProductos();
        return ResponseEntity.ok(productos);
    }

    // 🔹 Obtener producto por ID
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
