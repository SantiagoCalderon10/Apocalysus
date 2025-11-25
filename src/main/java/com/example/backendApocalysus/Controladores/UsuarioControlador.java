package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.DireccionDTO;
import com.example.backendApocalysus.Dto.UsuarioCrearDTO;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Seguridad.SecurityUtils;
import com.example.backendApocalysus.Seguridad.UserDetailsImpl;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios y sus direcciones")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {


    @Autowired
    private UsuarioServicio usuarioServicio;

    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario en el sistema (alternativa al endpoint de auth)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado")
    })
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioCrearDTO dto) {
        return ResponseEntity.ok(usuarioServicio.registrarUsuario(dto));
    }



    @Operation(
            summary = "Listar todos los usuarios (Admin)",
            description = "Obtiene listado completo de usuarios registrados. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioDTO.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioServicio.obtenerTodos());
    }


    @Operation(
            summary = "Obtener usuario por ID (Admin)",
            description = "Obtiene información de un usuario específico por su ID. Solo administradores."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuarioServicio.obtenerPorId(id));
    }



    @Operation(
            summary = "Obtener direcciones del usuario",
            description = "Lista todas las direcciones registradas del usuario autenticado"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Direcciones obtenidas exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DireccionDTO.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/direcciones")
    public ResponseEntity<List<DireccionDTO>> obtenerDireccionesPorUsuario() {
        int id = SecurityUtils.getUserId();
        return ResponseEntity.ok(usuarioServicio.obtenerDireccionesDTOporUsuario(id));
    }


    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Obtiene información del usuario actualmente autenticado"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UsuarioDTO usuarioDTO = usuarioServicio.obtenerUsuarioPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(usuarioDTO);
    }

    @Operation(
            summary = "Agregar dirección",
            description = "Agrega una nueva dirección al usuario autenticado"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dirección agregada exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DireccionDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/direcciones")
    public ResponseEntity<List<DireccionDTO>> agregarDireccion(@RequestBody DireccionDTO dto) {
        int idUsuario = SecurityUtils.getUserId();

        usuarioServicio.agregarDireccion(dto, idUsuario);
        List<DireccionDTO> direcciones = usuarioServicio.obtenerDireccionesDTOporUsuario(idUsuario);

        return ResponseEntity.ok(direcciones);
    }
}