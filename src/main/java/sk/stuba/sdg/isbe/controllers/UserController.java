package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.User;
import sk.stuba.sdg.isbe.services.UserService;
import sk.stuba.sdg.isbe.utilities.JwtUtils;

import java.util.List;
import java.util.ArrayList;


@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {return userService.getUsers();}

    @Operation(summary = "Create new user")
    @PostMapping("/create/{role}")
    public User createUser(@Valid @RequestBody User user,@PathVariable String role) {
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        user.setAuthorities(authorities);
        return userService.createUser(user);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/getUserById/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Login user by name and password")
    @PostMapping("/login/{name}/{password}")
    public ResponseEntity<?> loginUser(@PathVariable String name, @PathVariable String password){
        User user = userService.loginUser(name, password);
        if (user != null) {
            String token = JwtUtils.generateToken(user);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @Operation(summary = "Login user by google token")
    @PostMapping("/loginGoogle/{token}")
    public ResponseEntity<?> loginUserGoogle(@PathVariable String token){
        User user = userService.loginUserGoogle(token);
        if (user != null) {
            String jwtToken = JwtUtils.generateToken(user);
            return ResponseEntity.ok(jwtToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @Operation(summary = "Update user through json")
    @PostMapping("/updateUser/{userId}")
    public User updateUser(@PathVariable String userId, @Valid @RequestBody User changeUser){
        return userService.updateUser(userId, changeUser);
    }

    @Operation(summary = "Set user role")
    @PostMapping("/setRole/{userId}/{role}")
    public User setRoleUser(@PathVariable String userId, @PathVariable String role){
        return userService.setRoleUser(userId, role);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("deleteUser/{userId}")
    public User deleteUser(@PathVariable String userId){
        return userService.deleteUser(userId);
    }
}
