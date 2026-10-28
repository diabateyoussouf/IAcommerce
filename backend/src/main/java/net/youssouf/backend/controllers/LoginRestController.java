package net.youssouf.backend.controllers;

import net.youssouf.backend.dtos.LoginRequest;
import net.youssouf.backend.entities.AppUser;
import net.youssouf.backend.entities.Role;
import net.youssouf.backend.repositories.AppUserRepository;
import net.youssouf.backend.securities.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${app.frontend.url}")
public class LoginRestController {

    @Autowired
    private AppUserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        AppUser user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email non trouvé"));
        // tester si mot paasse saisi correspond avec celle stock
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Mot de passe invalide"));
        }

        String token = JwtUtil.generateToken(user);
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName) // CORRECT : Utilise Role::getName comme défini dans votre entité.
                .collect(Collectors.toList());

        Map<String, Object> userMap = Map.of(
                "id", user.getId(), // Assuming your AppUser has a getId() method
                "email", user.getEmail(),
                "username", user.getUsername(),
                "roles", roleNames

        );

        // FIX: Return a response body that includes BOTH the token and the user object
        Map<String, Object> responseBody = Map.of(
                "token", token,
                "user", userMap);

        System.out.println(responseBody);
        return ResponseEntity.ok(responseBody);

    }

}
