package com.hortina.api.web;

import com.hortina.api.domain.Usuario;
import com.hortina.api.repo.UsuarioRepository;
import com.hortina.api.web.dto.RegistroRequest;
import com.hortina.api.web.dto.UsuarioResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UsuarioController(UsuarioRepository repo, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping
    public List<Usuario> list() {
        return repo.findAll();
    }

    @GetMapping("/me")
    public UsuarioResponse perfil(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("No autenticado");
        }

        String email = authentication.getName();

        return repo.findByEmail(email)
                .map(u -> new UsuarioResponse(
                        u.getId_usuario(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getFecha_registro()))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody RegistroRequest request) {

        if (repo.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese mail");
        }

        Usuario u = new Usuario();
        u.setNombre(request.nombre());
        u.setEmail(request.email());
        u.setPassword_hash(passwordEncoder.encode(request.password()));
        u.setFecha_registro(LocalDate.now());

        return repo.save(u);
    }

}
