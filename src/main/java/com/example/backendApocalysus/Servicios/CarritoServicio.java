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

         // Eliminar del carrito en memoria
         carrito.getDetalles().remove(detalle);

         // Esto eliminará también la entidad de la BD si la relación tiene orphanRemoval=true
         // Si no, aún puedes hacer detalleCarritoRepositorio.delete(detalle);

         // Recalcular total del carrito
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

    @Transactional
    public CarritoDTO vaciarCarrito(int idUsuario) {

        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito activo no encontrado"));

        // Eliminar todos los detalles del carrito
        detalleCarritoRepositorio.deleteAll(carrito.getDetalles());

        // Limpiar la lista en memoria
        carrito.getDetalles().clear();

        // Recalcular total
        carrito.setTotal(0.0);

        // Guardar carrito actualizado
        carritoRepositorio.save(carrito);

        return convertirADTO(carrito);
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
        dto.setActivo(carrito.isActivo());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setTotal(carrito.getDetalles().stream()
                .mapToDouble(DetalleCarrito::getPrecioTotal)
                .sum());

        List<CarritoItemDTO> items = carrito.getDetalles().stream().map(det -> {
            CarritoItemDTO itemDTO = new CarritoItemDTO();
            itemDTO.setIdProducto(det.getProducto().getId());
            itemDTO.setNombre(det.getProducto().getNombre());
            itemDTO.setImagenUrl(det.getProducto().getImagenUrl());
            itemDTO.setCantidad(det.getCantidad());
            itemDTO.setPrecioUnitario(det.getPrecioUnitario());
            itemDTO.setSubtotal(det.getPrecioTotal());
            return itemDTO;
        }).toList();

        dto.setItems(items);
        return dto;
    }

}








