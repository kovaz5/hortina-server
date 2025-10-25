package com.hortina.api.web;

import com.hortina.api.domain.Usuario;
import com.hortina.api.repo.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<Usuario> list() {
        return repo.findAll();
    }

    @PostMapping
    public Usuario create(@RequestBody Usuario u) {

        // ciframos contraseña
        String hash = passwordEncoder.encode(u.getPassword_hash());
        u.setPassword_hash(hash);
        return repo.save(u);
    }
}
