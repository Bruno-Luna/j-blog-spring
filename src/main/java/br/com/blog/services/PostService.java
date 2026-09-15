package br.com.blog.services;

import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.PostModel;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.PostRepository;
import br.com.blog.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public void deletePost(PostModel postModel, String username) {
        postModel.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found")));

        PostModel post = postRepository.getById(postModel.getPostId());
        postRepository.delete(post);
    }
}
