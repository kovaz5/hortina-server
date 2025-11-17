package com.hortina.api.web;

import com.hortina.api.domain.Usuario;
import com.hortina.api.repo.UsuarioRepository;
import com.hortina.api.security.JwtUtil;
import com.hortina.api.web.dto.LoginRequest;
import com.hortina.api.web.dto.TokenResponse;
import com.hortina.api.web.dto.UsuarioResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password()));

            Usuario usuario = usuarioRepository.findByEmail(req.email()).orElseThrow();

            String access = jwtUtil.generateAccessToken(usuario.getEmail());
            String refresh = jwtUtil.generateRefreshToken(usuario.getEmail());

            TokenResponse res = new TokenResponse(
                    access,
                    refresh,
                    new UsuarioResponse(
                            usuario.getId_usuario(),
                            usuario.getNombre(),
                            usuario.getEmail(),
                            usuario.getFecha_registro()));

            return ResponseEntity.ok(res);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        String token = refreshToken.replace("\"", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        String username = jwtUtil.getUsernameFromToken(token);
        String newAccess = jwtUtil.generateAccessToken(username);
        String newRefresh = jwtUtil.generateRefreshToken(username);
        return ResponseEntity.ok(new TokenResponse(newAccess, newRefresh, null));
    }
}
