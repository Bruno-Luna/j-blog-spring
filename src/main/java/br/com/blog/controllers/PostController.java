package br.com.blog.controllers;

import br.com.blog.api.ApiResponse;
import br.com.blog.dto.PostRequestDTO;
import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import br.com.blog.services.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me/posts")
    public ResponseEntity<Object> getMyPosts(Authentication authentication) {
        UserModel user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user was not found"));

        List<PostResponseDTO> posts = postService.listAllPostByIdUser(user);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/by-filters")
    public ResponseEntity<Object> findPostsByFilters(Authentication authentication,
                                                @RequestParam(name = "title", required = false) String title,
                                                @RequestParam(name = "body", required = false) String body,
                                                @RequestParam(name = "username", required = false) String username) {
        UserModel user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user was not found"));

        List<PostResponseDTO> posts = postService.findPostsByFilters(title, body, username);

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(posts);
    }

    @PostMapping()
    public ResponseEntity<Object> insertPost(Authentication authentication,
                                             @RequestBody @Valid PostRequestDTO postRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse()
                        .status(HttpStatus.CREATED.value())
                        .message("Post created successfully")
                        .data("post", postService.savePost(postRequestDTO, authentication.getName())));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Object> editPost(Authentication authentication,
                                           @PathVariable("postId") UUID postId,
                                           @RequestBody PostRequestDTO postRequestDTO){

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse()
                        .status(HttpStatus.OK.value())
                        .message("Post updated successfully")
                        .data("post", postService.editPost(postId, postRequestDTO, authentication.getName())));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(Authentication authentication, @PathVariable("postId") UUID postId){

        postService.deletePost(postId, authentication.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse()
                        .status(HttpStatus.OK.value())
                        .message("Post deleted successfully"));
    }
}
