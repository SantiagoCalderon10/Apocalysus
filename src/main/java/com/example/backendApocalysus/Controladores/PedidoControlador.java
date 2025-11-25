package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.PedidoCreacionDTO;
import com.example.backendApocalysus.Dto.PedidoDTO;
import com.example.backendApocalysus.Dto.MetodosPagoDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Servicios.PedidoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Gestión de pedidos y órdenes de compra")

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {
    @Autowired
    private PedidoServicio pedidoServicio;


    @Operation(
            summary = "Crear nuevo pedido",
            description = "Crea un pedido a partir del carrito del usuario autenticado. Valida stock, actualiza inventario y vacía el carrito."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })

    @PostMapping("/nuevo")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoCreacionDTO dto) {

        System.out.println("🎯 LLEGÓ AL CONTROLADOR");

        int idUsuario = SecurityUtils.getUserId();  // ⬅️ ESTO se ejecuta DESPUÉS de validar

        System.out.println("👤 ID Usuario: " + idUsuario);

        dto.setIdUsuario(idUsuario);  // ⬅️ Pero el @Valid ya validó ANTES

        return ResponseEntity.ok(pedidoServicio.crearPedido(dto));
    }

    @Operation(
            summary = "Obtener historial de pedidos",
            description = "Obtiene todos los pedidos realizados por el usuario autenticado"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PedidoDTO.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/historial")
    public ResponseEntity<?> historial() {
        int idUsuario = SecurityUtils.getUserId();
        return ResponseEntity.ok(pedidoServicio.obtenerHistorial(idUsuario));
    }


    @Operation(
            summary = "Listar todos los pedidos (Admin)",
            description = "Obtiene todos los pedidos del sistema. Solo accesible para administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PedidoDTO.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos() {
        return ResponseEntity.ok(pedidoServicio.obtenerTodos());
    }
    @Operation(
            summary = "Obtener métodos de pago",
            description = "Lista todos los métodos de pago disponibles en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métodos de pago obtenidos exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MetodosPagoDTO.class))))
    })
    @GetMapping("/metodospago")
    public ResponseEntity<List<MetodosPagoDTO>> obtenerMetodosPago() {
        return ResponseEntity.ok(pedidoServicio.obtenerMetodosPago());
    }
}
