package br.com.blog.controllers;

import br.com.blog.api.ApiResult;
import br.com.blog.dto.UserRequestDTO;
import br.com.blog.models.UserModel;
import br.com.blog.services.JwtService;
import br.com.blog.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Registra um novo usuário",
            description = "Registra um novo usuário no sistema"
    )
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        if (userService.existsUsername(userRequestDTO).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResult()
                            .status(HttpStatus.CONFLICT.value())
                            .message("Username is already in use"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResult()
                        .status(HttpStatus.CREATED.value())
                        .message("User created successfully")
                        .data("user", userService.saveUser(userRequestDTO)));
    }

    @Operation(
            summary = "Autentica um usuário",
            description = "Autentica um usuário e retorna um token JWT se as credenciais forem válidas"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        UserModel user = userService.verifyUsername(userRequestDTO);

        if (user != null && userService.checkPassword(userRequestDTO.getPassword(), user.getPassword())) {
            String token = jwtService.generateToken(user.getUsername());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResult()
                            .status(HttpStatus.OK.value())
                            .message("Authentication successful")
                            .data("Bearer", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResult()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Invalid credentials"));
    }
}
