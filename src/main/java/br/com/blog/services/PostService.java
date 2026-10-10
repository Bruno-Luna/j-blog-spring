package br.com.blog.services;

import br.com.blog.dto.PostRequestDTO;
import br.com.blog.dto.PostResponseDTO;
import br.com.blog.models.PostModel;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.PostRepository;
import br.com.blog.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<PostResponseDTO> findPostsByFilters(String title, String body, String username) {

        Set<PostResponseDTO> postResponseDTOSet = new HashSet<>();

        if (Objects.nonNull(title) && !title.isEmpty()) {
            postResponseDTOSet.addAll(
                    postRepository.findByTitleContainingIgnoreCase(title)
                            .stream()
                            .map(PostResponseDTO::new)
                            .toList()
            );
        }

        if (Objects.nonNull(body) && !body.isEmpty()) {
            postResponseDTOSet.addAll(
                    postRepository.findByBodyContainingIgnoreCase(body)
                            .stream()
                            .map(PostResponseDTO::new)
                            .toList()
            );
        }

        if (Objects.nonNull(username) && !username.isEmpty()) {
            postResponseDTOSet.addAll(
                    postRepository.findByUser_UsernameContainingIgnoreCase(username)
                            .stream()
                            .map(PostResponseDTO::new)
                            .toList()
            );
        }

        return getPostResponseDTOS(title, body, username);
    }

    private List<PostResponseDTO> getPostResponseDTOS(String title, String body, String username) {
        List<PostResponseDTO> postResponseDTOList = Stream.of(
                        postRepository.findByTitleContainingIgnoreCase(title).stream(),
                        postRepository.findByBodyContainingIgnoreCase(body).stream(),
                        postRepository.findByUser_UsernameContainingIgnoreCase(username).stream()
                )
                .flatMap(Function.identity()) // junta todos os streams
                .map(PostResponseDTO::new)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(PostResponseDTO::getPostId, dto -> dto, (a, b) -> a),
                        map -> new ArrayList<>(map.values())
                ));
        return postResponseDTOList;
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
                    HttpStatus.FORBIDDEN, "You are not allowed to update this post");
        }
    }

    @Transactional
    public void deletePost(UUID postId, String username) {
        PostModel postSearch = postRepository.getById(postId);

        if(postSearch.getUser().getUsername().equals(username)) {
            postRepository.delete(postSearch);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not allowed to delete this post");
        }
    }

    private static PostResponseDTO getPostResponseDTO(PostModel postModel) {
        return new PostResponseDTO(
                postModel.getPostId(),
                postModel.getTitle(),
                postModel.getBody(),
                postModel.getUpdatedAt(),
                postModel.getUser().getUsername()
        );
    }
}
