package net.youssouf.backend.controllers;

import net.youssouf.backend.dtos.RegisterRequest;
import net.youssouf.backend.entities.AppUser;
import net.youssouf.backend.entities.Role;
import net.youssouf.backend.repositories.AppUserRepository;
import net.youssouf.backend.repositories.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${app.frontend.url}")
public class RegisterRestController {

    @Autowired
    private AppUserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        System.out.println("================================================");
        System.out.println("RegisterRequest reçu : " + req.getEmail());
        System.out.println("RegisterRequest reçu : " + req.getUsername());
        System.out.println("================================================");
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already in use"));
        }

        AppUser user = new AppUser();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        Role defaultRole = roleRepo.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(defaultRole);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

}
