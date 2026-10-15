package br.com.blog.controllers;

import br.com.blog.api.ApiResult;
import br.com.blog.dto.PostRequestDTO;
import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import br.com.blog.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
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
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Busca postagens de um usuário específico",
            description = "Retorna as postagens de um usuário específico"
    )
    @GetMapping("/me/posts")
    public ResponseEntity<Object> getMyPosts(Authentication authentication) {
        UserModel user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PostResponseDTO> posts = postService.listAllPostByIdUser(user);

        return posts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(posts);
    }

    @Operation(
            summary = "Busca postagens por filtros",
            description = "Retorna as postagens que correspondem aos filtros fornecidos (título, corpo, nome de usuário)"
    )
    @GetMapping("/by-filters")
    public ResponseEntity<Object> findPostsByFilters(Authentication authentication,
                                                @RequestParam(name = "title", required = false) String title,
                                                @RequestParam(name = "body", required = false) String body,
                                                @RequestParam(name = "username", required = false) String username) {

        List<PostResponseDTO> posts = postService.findPostsByFilters(title, body, username);

        return posts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(posts);
    }

    @Operation(
            summary = "Cria uma nova postagem",
            description = "Permite criar uma nova postagem, fornecendo o título e o corpo da postagem no corpo da requisição"
    )
    @PostMapping()
    public ResponseEntity<Object> insertPost(Authentication authentication,
                                             @RequestBody @Valid PostRequestDTO postRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResult()
                        .status(HttpStatus.CREATED.value())
                        .message("Post created successfully")
                        .data("post", postService.savePost(postRequestDTO, authentication.getName())));
    }

    @Operation(
            summary = "Edita uma postagem existente",
            description = "Permite editar uma postagem existente, fornecendo o ID da postagem e os novos dados (título e corpo) no corpo da requisição"
    )
    @PutMapping("/{postId}")
    public ResponseEntity<Object> editPost(Authentication authentication,
                                           @PathVariable("postId") UUID postId,
                                           @RequestBody PostRequestDTO postRequestDTO){

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResult()
                        .status(HttpStatus.OK.value())
                        .message("Post updated successfully")
                        .data("post", postService.editPost(postId, postRequestDTO, authentication.getName())));
    }

    @Operation(
            summary = "Deleta uma postagem existente",
            description = "Permite deletar uma postagem existente, fornecendo o ID da postagem no caminho da requisição"
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(Authentication authentication, @PathVariable("postId") UUID postId){

        postService.deletePost(postId, authentication.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResult()
                        .status(HttpStatus.OK.value())
                        .message("Post deleted successfully"));
    }
}
