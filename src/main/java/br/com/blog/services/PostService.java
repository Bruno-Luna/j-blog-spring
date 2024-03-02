package br.com.blog.services;

import br.com.blog.models.PostModel;
import br.com.blog.repositories.PostRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    public List<PostModel> listAllPost() {
        return postRepository.findAll();
    }

    @Transactional
    public PostModel savePost(PostModel postModel) {
        return postRepository.save(postModel);
    }

    @Transactional
    public PostModel editPost(PostModel postModel) {
        PostModel post = postRepository.getById(postModel.getPostId());
        BeanUtils.copyProperties(postModel, post);
        return postRepository.save(post);
    }

    @Transactional
    public void  deletePost(Map<String, String> postId) {
        UUID uuid = UUID.fromString(postId.get("postId"));
        PostModel post = postRepository.getById(uuid);
        postRepository.delete(post);
    }
}
