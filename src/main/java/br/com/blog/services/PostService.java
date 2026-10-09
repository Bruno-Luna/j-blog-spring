package br.com.blog.services;

import br.com.blog.dto.PostRequestDTO;
import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.PostModel;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.PostRepository;
import br.com.blog.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostResponseDTO> listAllPostByIdUser(UserModel userModel) {
        return postRepository.findAllByUser_UserId(userModel.getUserId())
                .stream()
                .map(PostResponseDTO::new)
                .toList();
    }

    @Transactional
    public PostResponseDTO savePost(PostRequestDTO postRequestDTO, String username) {
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        PostModel postModel = new PostModel();
        BeanUtils.copyProperties(postRequestDTO, postModel);
        postModel.setUser(userModel);
        postModel = postRepository.save(postModel);

        return getPostResponseDTO(postModel);
    }

    @Transactional
    public PostResponseDTO editPost(UUID postId, PostRequestDTO postRequestDTO, String username) {

        PostModel postSearch = postRepository.getById(postId);

        if(postSearch.getUser().getUsername().equals(username)) {
            BeanUtils.copyProperties(postRequestDTO, postSearch);
            postSearch = postRepository.save(postSearch);

            return getPostResponseDTO(postSearch);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to edit this post");
        }
    }

    @Transactional
    public void deletePost(UUID postId, String username) {
        PostModel postSearch = postRepository.getById(postId);

        if(postSearch.getUser().getUsername().equals(username)) {
            postRepository.delete(postSearch);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to delete this post");
        }
    }

    private static PostResponseDTO getPostResponseDTO(PostModel postModel) {
        return new PostResponseDTO(
                postModel.getPostId(),
                postModel.getTitle(),
                postModel.getBody(),
                postModel.getUpdatedAt()
        );
    }
}
