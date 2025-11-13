package com.example.backendApocalysus.Servicios;


import com.example.backendApocalysus.Dto.DireccionDTO;
import com.example.backendApocalysus.Dto.UsuarioCrearDTO;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Entidades.Carrito;
import com.example.backendApocalysus.Entidades.Pedido;
import com.example.backendApocalysus.Entidades.Rol;
import com.example.backendApocalysus.Entidades.Usuario;
import com.example.backendApocalysus.Repositorios.CarritoRepositorio;
import com.example.backendApocalysus.Repositorios.RolRepositorio;
import com.example.backendApocalysus.Repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private CarritoRepositorio carritoRepositorio;



    // 🧍 Crear nuevo usuario
    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioCrearDTO dto) {

        if (usuarioRepositorio.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con este correo");
        }

        Rol rol = rolRepositorio.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(dto.getContrasena());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);

        usuarioRepositorio.save(usuario);

        // Crear carrito vacío
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setActivo(true);
        carritoRepositorio.save(carrito);

        return convertirADTO(usuario);
    }

    // 🔍 Obtener todos los usuarios
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepositorio.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    // 🔍 Obtener usuario por ID
    public UsuarioDTO obtenerPorId(int id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(usuario);
    }

    // 🧩 Conversión entidad → DTO
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setRolNombre(usuario.getRol() != null ? usuario.getRol().getNombre() : null);

        if (usuario.getDirecciones() != null) {
            dto.setDirecciones(usuario.getDirecciones().stream().map(d -> {
                DireccionDTO direccionDTO = new DireccionDTO();
                direccionDTO.setId(d.getId());
                direccionDTO.setCalle(d.getCalle());
                direccionDTO.setCiudad(d.getCiudad());
                direccionDTO.setDepartamento(d.getDepartamento());
                direccionDTO.setPais(d.getPais());
                return direccionDTO;
            }).toList());
        }

        if (usuario.getCarrito() != null) {
            dto.setIdCarrito(usuario.getCarrito().getIdCarrito());
        }

        if (usuario.getPedidos() != null) {
            dto.setPedidosIds(usuario.getPedidos().stream().map(Pedido::getId).toList());
        }

        return dto;
    }
}
