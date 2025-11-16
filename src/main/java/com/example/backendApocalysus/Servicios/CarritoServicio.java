package com.example.backendApocalysus.Servicios;


import com.example.backendApocalysus.Dto.CarritoDTO;
import com.example.backendApocalysus.Dto.CarritoItemDTO;
import com.example.backendApocalysus.Entidades.Carrito;
import com.example.backendApocalysus.Entidades.DetalleCarrito;
import com.example.backendApocalysus.Entidades.Producto;
import com.example.backendApocalysus.Entidades.Usuario;
import com.example.backendApocalysus.Repositorios.CarritoRepositorio;
import com.example.backendApocalysus.Repositorios.DetalleCarritoRepositorio;
import com.example.backendApocalysus.Repositorios.ProductoRepositorio;
import com.example.backendApocalysus.Repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoServicio {

    @Autowired
    private CarritoRepositorio carritoRepositorio;

    @Autowired
    private DetalleCarritoRepositorio detalleCarritoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // 🛒 Agregar producto al carrito
    @Transactional
    public CarritoDTO agregarProducto(int idUsuario, int idProducto, int cantidad) {

        // 1️⃣ Buscar usuario
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2️⃣ Buscar producto
        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor que 0");
        }

        // Validar stock disponible
        if (producto.getCantidadDisponible() == null || producto.getCantidadDisponible() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + (producto.getCantidadDisponible() != null ? producto.getCantidadDisponible() : 0));
        }

        // 3️⃣ Obtener o crear carrito activo (asegurar solo uno activo por usuario)
        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseGet(() -> {
                    // Desactivar carritos anteriores si existen
                    List<Carrito> carritosAnteriores = carritoRepositorio.findAllByUsuarioAndActivoTrue(usuario);
                    carritosAnteriores.forEach(c -> {
                        c.setActivo(false);
                        carritoRepositorio.save(c);
                    });

                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    nuevo.setActivo(true);
                    return carritoRepositorio.save(nuevo);
                });

        // 4️⃣ Buscar si el producto ya está en el carrito
        DetalleCarrito detalle = detalleCarritoRepositorio.findByCarritoAndProducto(carrito, producto)
                .orElse(null);

        if (detalle != null) {
            // Verificar stock para cantidad adicional
            int cantidadTotal = detalle.getCantidad() + cantidad;
            if (producto.getCantidadDisponible() < cantidadTotal) {
                throw new RuntimeException("Stock insuficiente para la cantidad total. Disponible: " + producto.getCantidadDisponible());
            }
            detalle.setCantidad(cantidadTotal);
            detalle.setPrecioTotal(detalle.getCantidad() * detalle.getPrecioUnitario());
        } else {
            DetalleCarrito nuevoDetalle = new DetalleCarrito();
            nuevoDetalle.setCarrito(carrito);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            nuevoDetalle.setPrecioTotal(producto.getPrecio() * cantidad);
            detalleCarritoRepositorio.save(nuevoDetalle);
        }

        // Recalcular total del carrito
        double total = carrito.getDetalles().stream()
                .mapToDouble(DetalleCarrito::getPrecioTotal)
                .sum();
        carrito.setTotal(total);

        return convertirADTO(carritoRepositorio.save(carrito));
    }

    // 🗑️ Eliminar un producto del carrito
    @Transactional
    public CarritoDTO eliminarProducto(int idUsuario, int idProducto) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito activo no encontrado"));

        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleCarrito detalle = detalleCarritoRepositorio.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("El producto no está en el carrito"));

        detalleCarritoRepositorio.delete(detalle);

        // Recalcular total del carrito después de eliminar
        double total = carrito.getDetalles().stream()
                .mapToDouble(DetalleCarrito::getPrecioTotal)
                .sum();
        carrito.setTotal(total);

        return convertirADTO(carritoRepositorio.save(carrito));
    }

    // 🔄 Actualizar cantidad de producto en carrito
    @Transactional
    public CarritoDTO actualizarCantidad(int idUsuario, int idProducto, int nuevaCantidad) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito activo no encontrado"));

        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (nuevaCantidad < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        }

        DetalleCarrito detalle = detalleCarritoRepositorio.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("El producto no está en el carrito"));

        if (nuevaCantidad == 0) {
            // Eliminar el producto del carrito
            detalleCarritoRepositorio.delete(detalle);
        } else {
            // Validar stock disponible
            if (producto.getCantidadDisponible() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getCantidadDisponible());
            }
            detalle.setCantidad(nuevaCantidad);
            detalle.setPrecioTotal(nuevaCantidad * detalle.getPrecioUnitario());
            detalleCarritoRepositorio.save(detalle);
        }

        // Recalcular total del carrito
        double total = carrito.getDetalles().stream()
                .mapToDouble(DetalleCarrito::getPrecioTotal)
                .sum();
        carrito.setTotal(total);

        return convertirADTO(carritoRepositorio.save(carrito));
    }

    // 🧹 Vaciar carrito
    @Transactional
    public void vaciarCarrito(int idUsuario) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito activo no encontrado"));

        detalleCarritoRepositorio.deleteAll(carrito.getDetalles());
    }

    // 📦 Obtener carrito actual
    public CarritoDTO obtenerCarritoPorUsuario(int idUsuario) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito activo no encontrado"));

        return convertirADTO(carrito);
    }

    // 🔄 Conversión a DTO
    private CarritoDTO convertirADTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setIdCarrito(carrito.getIdCarrito());
        dto.setIdUsuario(carrito.getUsuario().getIdUsuario());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setActivo(carrito.isActivo());

        List<CarritoItemDTO> items = carrito.getDetalles().stream()
                .map(detalle -> {
                    CarritoItemDTO item = new CarritoItemDTO();
                    item.setIdProducto(detalle.getProducto().getId());
                    item.setCantidad(detalle.getCantidad());
                    item.setPrecioUnitario(detalle.getPrecioUnitario());
                    item.setSubtotal(detalle.getPrecioTotal());
                    return item;
                }).collect(Collectors.toList());

        dto.setItems(items);
        // Usar el total calculado de la entidad en lugar de recalcular
        dto.setTotal(carrito.getTotal() != null ? carrito.getTotal() : 0.0);
        return dto;
    }
}








