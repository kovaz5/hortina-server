package com.hortina.api.web;

import com.hortina.api.domain.MyUserDetails;
import com.hortina.api.domain.Usuario;
import com.hortina.api.repo.UsuarioRepository;
import com.hortina.api.web.dto.LoginRequest;
import com.hortina.api.web.dto.RegistroRequest;
import com.hortina.api.web.dto.UsuarioResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.core.Authentication;
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
    public UsuarioResponse perfil(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return repo.findByEmail(principal.getUsername())
                .map(u -> new UsuarioResponse(u.getId_usuario(), u.getNombre(), u.getEmail(), u.getFecha_registro()))
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

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            MyUserDetails principal = (MyUserDetails) auth.getPrincipal();
            var usuario = principal.getUsuario();

            return ResponseEntity.ok(
                    new UsuarioResponse(
                            usuario.getId_usuario(),
                            usuario.getNombre(),
                            usuario.getEmail(),
                            usuario.getFecha_registro()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
