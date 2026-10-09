package br.com.blog.services;

import br.com.blog.api.ApiResponse;
import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.PostModel;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.PostRepository;
import br.com.blog.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    public List<PostResponseDTO> listAllPostByIdUser(UserModel userModel) {
        return postRepository.findAllByUser_UserId(userModel.getUserId())
                .stream()
                .map(PostResponseDTO::new)
                .toList();
    }

    @Transactional
    public PostResponseDTO savePost(PostModel postModel, String username) {
        postModel.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found")));
        postModel.setLocalDateTime(LocalDateTime.now());
        postRepository.save(postModel);

        return new PostResponseDTO(
                postModel.getPostId(),
                postModel.getTitle(),
                postModel.getBody(),
                postModel.getLocalDateTime()
        );
    }

    @Transactional
    public PostResponseDTO editPost(PostModel postModel, String username) {
        postModel.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found")));

        PostModel post = postRepository.getById(postModel.getPostId());
        postModel.setLocalDateTime(LocalDateTime.now());
        BeanUtils.copyProperties(postModel, post);
        postRepository.save(post);

        return new PostResponseDTO(
                post.getPostId(),
                post.getTitle(),
                post.getBody(),
                post.getLocalDateTime()
        );
    }

    @Transactional
    public void deletePost(UUID postId, String username) {
        PostModel postSearch = postRepository.getById(postId);

        if(postSearch.getUser().getUsername().equals(username)) {
            postRepository.delete(postSearch);
        }else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to delete this post");
        }
    }
}
