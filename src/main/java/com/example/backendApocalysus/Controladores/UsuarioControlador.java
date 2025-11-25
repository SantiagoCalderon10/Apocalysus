package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.DireccionDTO;
import com.example.backendApocalysus.Dto.UsuarioCrearDTO;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Seguridad.UserDetailsImpl;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/listar")
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

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UsuarioDTO usuarioDTO = usuarioServicio.obtenerUsuarioPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(usuarioDTO);
    }


    @PostMapping("/direcciones")
    public ResponseEntity<List<DireccionDTO>> agregarDireccion(@RequestBody DireccionDTO dto) {
        int idUsuario = SecurityUtils.getUserId();

        usuarioServicio.agregarDireccion(dto, idUsuario);
        List<DireccionDTO> direcciones = usuarioServicio.obtenerDireccionesDTOporUsuario(idUsuario);

        return ResponseEntity.ok(direcciones);
    }
}