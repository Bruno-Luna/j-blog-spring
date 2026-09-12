package br.com.blog.controllers;

import br.com.blog.api.ApiResponse;
import br.com.blog.models.UserModel;
import br.com.blog.services.JwtService;
import br.com.blog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody @Valid UserModel userModel) {
        if (userService.existsUsername(userModel).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse()
                            .status(HttpStatus.CONFLICT.value())
                            .message("Username is already in use"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse()
                        .status(HttpStatus.CREATED.value())
                        .message("User created with success")
                        .data("user", userService.saveUser(userModel)));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserModel userModel) {
        UserModel user = userService.verifyUsername(userModel);

        if (user != null && userService.checkPassword(userModel.getPassword(), user.getPassword())) {
            String token = jwtService.generateToken(user.getUsername());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse()
                            .status(HttpStatus.OK.value())
                            .message("Login success")
                            .data("token", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Invalid credentials"));
    }
}
