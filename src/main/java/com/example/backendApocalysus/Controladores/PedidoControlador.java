package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.PedidoCreacionDTO;
import com.example.backendApocalysus.Dto.PedidoDTO;
import com.example.backendApocalysus.Servicios.PedidoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {


    @Autowired
    private PedidoServicio pedidoServicio;

    @PostMapping("/crear")
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoCreacionDTO dto) {
        return ResponseEntity.ok(pedidoServicio.crearPedido(dto));
    }

    @GetMapping("/historial/{idUsuario}")
    public ResponseEntity<List<PedidoDTO>> historial(@PathVariable int idUsuario) {
        return ResponseEntity.ok(pedidoServicio.obtenerHistorial(idUsuario));
    }
}
