package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Servicios.PedidoServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;


import java.util.HashMap;
import java.util.Map;
@Tag(name = "Administración", description = "Endpoints administrativos para estadísticas y reportes")

@RestController
@RequestMapping("/api/admin")
public class AdminControlador {

    @Autowired
    ProductoServicio productoServicio;

    @Autowired
    UsuarioServicio usuarioServicio;

    @Autowired
    PedidoServicio pedidoServicio;


    @Operation(
            summary = "Obtener estadísticas generales (Admin)",
            description = "Retorna estadísticas generales del sistema: total de usuarios, productos y pedidos. Solo administradores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas exitosamente",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"usuarios\": 150, \"productos\": 45, \"pedidos\": 320}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("usuarios", usuarioServicio.contarUsuarios());
        stats.put("productos", productoServicio.contarProductos());
        stats.put("pedidos", pedidoServicio.contarPedidos());

        return stats;
    }
}
