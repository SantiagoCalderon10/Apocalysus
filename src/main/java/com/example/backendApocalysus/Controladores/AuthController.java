package com.example.backendApocalysus.Controladores;

import com.example.backendApocalysus.Dto.AuthResponseDTO;
import com.example.backendApocalysus.Dto.LoginRequest;
import com.example.backendApocalysus.Dto.RegisterRequest;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Entidades.Usuario;
import com.example.backendApocalysus.Seguridad.JwtUtils;
import com.example.backendApocalysus.Seguridad.UserDetailsImpl;
import com.example.backendApocalysus.Servicios.UsuarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Endpoints para registro, login y verificación de usuarios")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private JwtUtils jwtUtils;

    // ---------------------
    // LOGIN
    // ---------------------
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario con email y contraseña, retorna un token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar token
            String token = jwtUtils.generateJwtToken((UserDetailsImpl) authentication.getPrincipal());

            // Obtener detalles del usuario
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Construir respuesta
            AuthResponseDTO response = new AuthResponseDTO(
                    token,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getAuthorities()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas: " + e.getMessage());
        }
    }

    // ---------------------
    // REGISTRO
    // ---------------------
    @Operation(
            summary = "Registrar usuario",
            description = "Crea una nueva cuenta de usuario en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Email ya registrado o datos inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {

            if (usuarioServicio.existePorCorreo(registerRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body("Error: El correo ya está registrado");
            }

            // Registrar usuario
            usuarioServicio.registrarUsuarioDesdeAuth(registerRequest);

            return ResponseEntity.ok("Usuario registrado con éxito");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    // ---------------------
    // VERIFICAR TOKEN
    // ---------------------
    @Operation(
            summary = "Verificar token",
            description = "Valida si un token JWT es válido y no ha expirado"
    )
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtils.validateJwtToken(token)) {
                String email = jwtUtils.getUserNameFromJwtToken(token);
                return ResponseEntity.ok(usuarioServicio.existePorCorreo(email));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación");
        }
    }


}
