package com.hortina.api.web;

import com.hortina.api.domain.Usuario;
import com.hortina.api.repo.UsuarioRepository;
import com.hortina.api.security.JwtUtil;
import com.hortina.api.web.dto.GoogleLoginRequest;
import com.hortina.api.web.dto.GoogleTokenInfo;
import com.hortina.api.web.dto.LoginRequest;
import com.hortina.api.web.dto.TokenResponse;
import com.hortina.api.web.dto.UsuarioResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest req) {

        System.out.println(">>> Google login recibido");

        if (req.idToken() == null || req.idToken().isBlank()) {
            return ResponseEntity.badRequest().body("Missing idToken");
        }

        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + req.idToken();

        RestTemplate rest = new RestTemplate();
        GoogleTokenInfo info;

        try {
            info = rest.getForObject(url, GoogleTokenInfo.class);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Google token");
        }

        if (!info.aud().equals("228270332646-10am94au55vg8nd7o19n3hepjkkocts3.apps.googleusercontent.com")) {
            return ResponseEntity.status(401).body("Invalid audience (aud)");
        }

        Usuario usuario = usuarioRepository.findByEmail(info.email()).orElse(null);

        if (usuario == null) {
            usuario = Usuario.builder()
                    .email(info.email())
                    .nombre(info.name() != null ? info.name() : info.email())
                    .password_hash("GOOGLE_USER")
                    .build();
            usuarioRepository.save(usuario);
        }

        String access = jwtUtil.generateAccessToken(usuario.getEmail());
        String refresh = jwtUtil.generateRefreshToken(usuario.getEmail());

        UsuarioResponse userRes = new UsuarioResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getFecha_registro());

        return ResponseEntity.ok(new TokenResponse(access, refresh, userRes));
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
