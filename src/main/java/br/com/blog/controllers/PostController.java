package br.com.blog.controllers;

import br.com.blog.models.PostModel;
import br.com.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;

    @GetMapping()
    ResponseEntity<Object> getAllPosts(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.listAllPost());
    }

    @GetMapping("/{userId}")
    ResponseEntity<Object> getAllPostsByIdUser(@PathVariable("userId") String userId){

        return ResponseEntity.status(HttpStatus.OK).build(postService.listAllPostByIdUser(userId));
    }

    @PostMapping()
    public ResponseEntity<Object> insertPost(@RequestBody @Valid PostModel postModel){
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(postModel));
    }

    @PutMapping()
    public ResponseEntity<Object> editPost(@RequestBody @Valid PostModel postModel){
        return ResponseEntity.status(HttpStatus.OK).body(postService.editPost(postModel));
    }

    @DeleteMapping()
    public void deletePost(@RequestBody @Valid Map<String, String> postId){
        postService.deletePost(postId);
    }
}
