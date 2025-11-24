package com.example.backendApocalysus.Servicios;


import com.example.backendApocalysus.Dto.DireccionDTO;
import com.example.backendApocalysus.Dto.RegisterRequest;
import com.example.backendApocalysus.Dto.UsuarioCrearDTO;
import com.example.backendApocalysus.Dto.UsuarioDTO;
import com.example.backendApocalysus.Entidades.*;
import com.example.backendApocalysus.Repositorios.CarritoRepositorio;
import com.example.backendApocalysus.Repositorios.DireccionRepositorio;
import com.example.backendApocalysus.Repositorios.RolRepositorio;
import com.example.backendApocalysus.Repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServicio {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private CarritoRepositorio carritoRepositorio;
    @Autowired
    private DireccionRepositorio direccionRepositorio;


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

        // 👉 Convertir y asignar direcciones

        usuarioRepositorio.save(usuario);

        // Crear carrito vacío

        if(dto.getIdRol() != 1){
            Carrito carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito.setActivo(true);
            carrito.setTotal(0.0);
            carritoRepositorio.save(carrito);
        }


        return convertirADTO(usuario);
    }

    public boolean existePorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo).isPresent();
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

    // ✏️ Actualizar usuario
    @Transactional
    public UsuarioDTO actualizarUsuario(int id, UsuarioCrearDTO dto) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el correo ya existe en otro usuario
        usuarioRepositorio.findByCorreo(dto.getCorreo()).ifPresent(u -> {

        });

        Rol rol = rolRepositorio.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);

        usuarioRepositorio.save(usuario);
        return convertirADTO(usuario);
    }

    // 🗑️ Eliminar usuario
    @Transactional
    public void eliminarUsuario(int id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepositorio.delete(usuario);
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


    public long contarUsuarios() {
        return usuarioRepositorio.count();
    }

    private DireccionDTO convertirADTO(Direccion direccion) {
        DireccionDTO dto = new DireccionDTO();
        dto.setId(direccion.getId());
        dto.setCalle(direccion.getCalle());
        dto.setCiudad(direccion.getCiudad());
        dto.setDepartamento(direccion.getDepartamento());
        dto.setPais(direccion.getPais());
        return dto;
    }

    private List<DireccionDTO> convertirListaADTO(List<Direccion> direcciones) {
        List<DireccionDTO> dtos = new ArrayList<>();
        for (Direccion direccion : direcciones) {
            dtos.add(convertirADTO(direccion));
        }
        return dtos;
    }
    public List<DireccionDTO> obtenerDireccionesDTOporUsuario(int idUsuario) {

        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Direccion> direcciones = direccionRepositorio.findByUsuario(usuario);

        List<DireccionDTO> dtos = new ArrayList<>();

        for (Direccion direccion : direcciones) {
            dtos.add(convertirADTO(direccion));
        }

        return dtos;
    }

    public void agregarDireccion(DireccionDTO dto, int idUsuario){

        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(()->new RuntimeException("Usuario no encontrado"));

        Direccion direccion = new Direccion();

        direccion.setUsuario(usuario);
        direccion.setCalle(dto.getCalle());
        direccion.setCiudad(dto.getCiudad());
        direccion.setDepartamento(dto.getDepartamento());
        direccion.setPais(dto.getPais());

        direccionRepositorio.save(direccion);

    }

    @Transactional
    public void registrarUsuarioDesdeAuth(RegisterRequest registerRequest) {

        // Buscar rol por defecto (Cliente = 2)
        Rol rol = rolRepositorio.findById(2)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellido(registerRequest.getApellido());
        usuario.setCorreo(registerRequest.getEmail());
        usuario.setContrasena(passwordEncoder.encode(registerRequest.getContrasena()));
        usuario.setTelefono(String.valueOf(registerRequest.getTelefono()));
        usuario.setRol(rol);

        usuarioRepositorio.save(usuario);

        // Crear carrito vacío para el usuario
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setActivo(true);
        carrito.setTotal(0.0);
        carritoRepositorio.save(carrito);
    }

}
