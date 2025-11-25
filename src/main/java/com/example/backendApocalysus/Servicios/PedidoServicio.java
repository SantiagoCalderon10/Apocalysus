package com.example.backendApocalysus.Servicios;

import com.example.backendApocalysus.Dto.MetodosPagoDTO;
import com.example.backendApocalysus.Dto.PedidoCreacionDTO;
import com.example.backendApocalysus.Dto.PedidoDTO;
import com.example.backendApocalysus.Dto.PedidoItemDTO;
import com.example.backendApocalysus.Entidades.*;
import com.example.backendApocalysus.Repositorios.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private DireccionRepositorio direccionRepositorio;

    @Autowired
    private MetodoPagoRepositorio metodoPagoRepositorio;

    @Autowired
    private CarritoRepositorio carritoRepositorio;

    @Autowired
    private DetalleCarritoRepositorio detalleCarritoRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Transactional
    public PedidoDTO crearPedido(PedidoCreacionDTO dto) {

        // 1️⃣ Validaciones
        Usuario usuario = usuarioRepositorio.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Direccion direccion = direccionRepositorio.findById(dto.getIdDireccion())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        MetodoPago metodoPago = metodoPagoRepositorio.findById(dto.getIdMetodoPago())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        Carrito carrito = carritoRepositorio.findByUsuarioAndActivoTrue(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito vacío o no encontrado"));

        if (carrito.getDetalles().isEmpty()) {
            throw new RuntimeException("No puedes crear un pedido con el carrito vacío");
        }

        // 2️⃣ Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDireccion(direccion);
        pedido.setMetodoPago(metodoPago);
        pedido.setCodigoPedido("APO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        double total = 0;
        pedido = pedidoRepositorio.save(pedido);

        // 3️⃣ Pasar detalles del carrito al pedido
        for (DetalleCarrito detCarrito : carrito.getDetalles()) {

            Producto producto = detCarrito.getProducto();

            if (producto.getCantidadDisponible() < detCarrito.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            DetallePedido detPedido = new DetallePedido();
            detPedido.setPedido(pedido);
            detPedido.setProducto(producto);
            detPedido.setCantidad(detCarrito.getCantidad());
            detPedido.setPrecioUnitario(detCarrito.getPrecioUnitario());

            double subtotal = detCarrito.getCantidad() * detCarrito.getPrecioUnitario();
            total += subtotal;

            detallePedidoRepositorio.save(detPedido);
            pedido.getDetalles().add(detPedido);

            // Reducir stock
            producto.setCantidadDisponible(producto.getCantidadDisponible() - detCarrito.getCantidad());
            productoRepositorio.save(producto);
        }

        // 4️⃣ Establecer total del pedido
        pedido.setPrecioTotal(total);
        pedidoRepositorio.save(pedido);

        // 5️⃣ Vaciar carrito correctamente SIN causar ObjectDeletedException
        carrito.getDetalles().clear(); // <-- Esto elimina automáticamente por orphanRemoval

        carrito.setTotal(0.0);
        carrito.setActivo(true);
        carritoRepositorio.save(carrito);

        return convertirPedidoADTO(pedido);
    }

    private PedidoDTO convertirPedidoADTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getId());
        dto.setCodigoPedido(pedido.getCodigoPedido());
        dto.setPrecioTotal(pedido.getPrecioTotal());
        dto.setNombreUsuario(pedido.getUsuario().getNombre() + "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setFecha(pedido.getFechaCreacion().format(formatter));

        dto.setDireccion(pedido.getDireccion().getCalle() + " - " + pedido.getDireccion().getCiudad());
        dto.setMetodoPago(pedido.getMetodoPago().getNombre());

        List<PedidoItemDTO> items = pedido.getDetalles().stream().map(det -> {
            PedidoItemDTO i = new PedidoItemDTO();
            i.setImagenUrl(det.getProducto().getImagenUrl());
            i.setNombreProducto(det.getProducto().getNombre());
            i.setCantidad(det.getCantidad());
            i.setPrecioUnitario(det.getPrecioUnitario());
            i.setSubtotal(det.getCantidad() * det.getPrecioUnitario());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }



    public List<PedidoDTO> obtenerTodos() {
        List<Pedido> pedidos = pedidoRepositorio.findAll();
        return pedidos.stream()
                .map(this::convertirPedidoADTO)
                .toList();
    }

    public List<PedidoDTO> obtenerHistorial(int idUsuario) {
        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return pedidoRepositorio.findByUsuario(usuario)
                .stream()
                .map(this::convertirPedidoADTO)
                .collect(Collectors.toList());
    }



    public long contarPedidos() {
        return pedidoRepositorio.count();
    }



    public List<MetodosPagoDTO> obtenerMetodosPago() {
        return metodoPagoRepositorio.findAll()
                .stream()
                .map(mp -> new MetodosPagoDTO(mp.getId(), mp.getNombre()))
                .toList();
    }





}
