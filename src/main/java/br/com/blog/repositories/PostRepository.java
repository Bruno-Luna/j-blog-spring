package br.com.blog.repositories;

import br.com.blog.models.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostModel, UUID> {

    List<PostModel> findAllByUser_UserId(UUID userId);

}
