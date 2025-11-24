package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.DireccionDTO;
import com.example.backendApocalysus.Dto.UsuarioCrearDTO;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {


    @Autowired
    private UsuarioServicio usuarioServicio;

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioCrearDTO dto) {
        return ResponseEntity.ok(usuarioServicio.registrarUsuario(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuarioServicio.obtenerPorId(id));
    }

    @GetMapping("/direcciones")
    public ResponseEntity<List<DireccionDTO>> obtenerDireccionesPorUsuario() {
        int id = SecurityUtils.getUserId();
        return ResponseEntity.ok(usuarioServicio.obtenerDireccionesDTOporUsuario(id));
    }

    @PostMapping("/direcciones")
    public ResponseEntity<List<DireccionDTO>> agregarDireccion(@RequestBody DireccionDTO dto) {
        int idUsuario = SecurityUtils.getUserId();

        usuarioServicio.agregarDireccion(dto, idUsuario);
        List<DireccionDTO> direcciones = usuarioServicio.obtenerDireccionesDTOporUsuario(idUsuario);

        return ResponseEntity.ok(direcciones);
    }
}