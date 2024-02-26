package br.com.blog.controllers;

import br.com.blog.models.UserModel;
import br.com.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody @Valid UserModel userModel){
        if(userService.existsUsername(userModel).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already in use!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userModel));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid UserModel userModel) {
        UserModel user = userService.verifyUsername(userModel);

        if (user != null) {
            Boolean passwordCorrect = userService.existsPassword(userModel.getPassword(), user.getPassword());

            if (passwordCorrect) {
                return ResponseEntity.status(HttpStatus.OK).body("Login, success!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Password incorrect!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
}
