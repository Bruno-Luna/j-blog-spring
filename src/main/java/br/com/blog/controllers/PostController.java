package br.com.blog.controllers;

import br.com.blog.models.PostModel;
import br.com.blog.models.UserModel;
import br.com.blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/post")
public class PostController {

    @Autowired
    UserService userService;

    @PostMapping()
    public ResponseEntity<Object> insertPost(@RequestBody @Valid PostModel postModel){

        System.out.println(postModel.getBody());
        System.out.println(postModel.getTitle());
        System.out.println(postModel.getUser().getUserId());

        return null;
    }

}
