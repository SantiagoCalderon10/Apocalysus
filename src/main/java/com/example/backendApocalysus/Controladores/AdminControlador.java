package com.example.backendApocalysus.Controladores;


import com.example.backendApocalysus.Servicios.PedidoServicio;
import com.example.backendApocalysus.Servicios.ProductoServicio;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminControlador {

    @Autowired
    ProductoServicio productoServicio;

    @Autowired
    UsuarioServicio usuarioServicio;

    @Autowired
    PedidoServicio pedidoServicio;

    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("usuarios", usuarioServicio.contarUsuarios());
        stats.put("productos", productoServicio.contarProductos());
        stats.put("pedidos", pedidoServicio.contarPedidos());

        return stats;
    }
}
