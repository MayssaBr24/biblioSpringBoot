package tn.essat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import tn.essat.config.JwtRequest;
import tn.essat.config.JwtResponse;
import tn.essat.config.GestionToken;
import tn.essat.dao.IUser;
import tn.essat.dao.IRole;
import tn.essat.model.User;
import tn.essat.model.Role;
import tn.essat.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/auth")
public class RestAuth {

    @Autowired
    private GestionToken token_gen;

    @Autowired
    private UserService userService;

    @Autowired
    private IUser userDao;

    @Autowired
    private IRole roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public JwtResponse signIn(@RequestBody JwtRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) userService.loadUserByUsername(request.getUsername());
        String token = token_gen.generateToken(user);

        return new JwtResponse(token, user);
    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userDao.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = (User) userService.loadUserByUsername(username);

        // Pour ne pas exposer le mot de passe
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userDao.existsByUsername(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

            // Encoder le mot de passe
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Si le rôle n'est pas fourni, attribuer le rôle USER par défaut
            if (user.getRole() == null) {
                Optional<Role> userRole = roleDao.findByRole("USER_ROLE");
                userRole.ifPresent(user::setRole);
            }

            // Activer le compte par défaut
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            User savedUser = userDao.save(user);
            savedUser.setPassword(null); // Ne pas renvoyer le mot de passe
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        try {
            Optional<User> userOptional = userDao.findById(id);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();

            // Mettre à jour les champs
            user.setName(userDetails.getName());
            user.setUsername(userDetails.getUsername());

            // Mettre à jour le mot de passe seulement s'il est fourni
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            // Mettre à jour le rôle
            if (userDetails.getRole() != null) {
                user.setRole(userDetails.getRole());
            }

            user.setEnabled(userDetails.isEnabled());

            User updatedUser = userDao.save(user);
            updatedUser.setPassword(null); // Ne pas renvoyer le mot de passe

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/users/{id}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Integer id) {
        try {
            Optional<User> userOptional = userDao.findById(id);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            user.setEnabled(!user.isEnabled());

            User updatedUser = userDao.save(user);
            updatedUser.setPassword(null); // Ne pas renvoyer le mot de passe

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        try {
            Optional<User> userOptional = userDao.findById(id);
            if (!userOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            userDao.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Méthodes manquantes pour la recherche d'utilisateurs
    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userDao.findByNameContainingOrUsernameContaining(query, query);
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/role/{roleName}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        List<User> users = userDao.findByRole_Role(roleName);
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    // Méthode pour compter les utilisateurs
    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Long>> countUsers() {
        long total = userDao.count();
        long admins = userDao.countByRole_Role("ADMIN_ROLE");
        long users = userDao.countByRole_Role("USER_ROLE");
        long enabled = userDao.countByEnabled(true);
        long disabled = userDao.countByEnabled(false);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("admins", admins);
        stats.put("users", users);
        stats.put("enabled", enabled);
        stats.put("disabled", disabled);

        return ResponseEntity.ok(stats);
    }
}