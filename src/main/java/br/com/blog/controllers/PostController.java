package br.com.blog.controllers;

import br.com.blog.api.ApiResponse;
import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.PostModel;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import br.com.blog.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/me/posts")
    public ResponseEntity<Object> getMyPosts(Authentication authentication) {
        UserModel user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        List<PostResponseDTO> posts = postService.listAllPostByIdUser(user);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    @PostMapping()
    public ResponseEntity<Object> insertPost(Authentication authentication,
                                             @RequestBody @Valid PostModel postModel){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse()
                        .status(HttpStatus.CREATED.value())
                        .message("Post created with success")
                        .data("post", postService.savePost(postModel, authentication.getName())));
    }

    @PutMapping()
    public ResponseEntity<Object> editPost(Authentication authentication,
                                           @RequestBody @Valid PostModel postModel){

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse()
                        .status(HttpStatus.OK.value())
                        .message("Post edited with success")
                        .data("post", postService.editPost(postModel, authentication.getName())));
    }

    @DeleteMapping()
    public ResponseEntity<Object> deletePost(Authentication authentication, @RequestBody @Valid PostModel postModel){
        postService.deletePost(postModel, authentication.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse()
                        .status(HttpStatus.OK.value())
                        .message("Post deleted successfully"));
    }
}
