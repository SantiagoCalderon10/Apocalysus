package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.PedidoCreacionDTO;
import com.example.backendApocalysus.Dto.PedidoDTO;
import com.example.backendApocalysus.Entidades.MetodoPago;
import com.example.backendApocalysus.Entidades.MetodosPagoDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Servicios.PedidoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {
    @Autowired
    private PedidoServicio pedidoServicio;

    @PostMapping("/nuevo")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoCreacionDTO dto) {

        System.out.println("🎯 LLEGÓ AL CONTROLADOR");

        int idUsuario = SecurityUtils.getUserId();  // ⬅️ ESTO se ejecuta DESPUÉS de validar

        System.out.println("👤 ID Usuario: " + idUsuario);

        dto.setIdUsuario(idUsuario);  // ⬅️ Pero el @Valid ya validó ANTES

        return ResponseEntity.ok(pedidoServicio.crearPedido(dto));
    }

    @GetMapping("/historial")
    public ResponseEntity<?> historial() {
        int idUsuario = SecurityUtils.getUserId();
        return ResponseEntity.ok(pedidoServicio.obtenerHistorial(idUsuario));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos() {
        return ResponseEntity.ok(pedidoServicio.obtenerTodos());
    }

    @GetMapping("/metodospago")
    public ResponseEntity<List<MetodosPagoDTO>> obtenerMetodosPago() {
        return ResponseEntity.ok(pedidoServicio.obtenerMetodosPago());
    }
}
