package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.UsuarioCrearDTO;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {



    @Autowired
    private UsuarioServicio usuarioServicio;

    // Crear nuevo usuario
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioCrearDTO dto) {
        return ResponseEntity.ok(usuarioServicio.registrarUsuario(dto));
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioServicio.obtenerTodos());
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuarioServicio.obtenerPorId(id));
    }

}