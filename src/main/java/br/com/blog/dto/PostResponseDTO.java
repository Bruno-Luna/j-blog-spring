package br.com.blog.dto;

import br.com.blog.models.PostModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class PostResponseDTO {

    private UUID postId;

    private String title;

    private String body;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt_BR")
    private LocalDateTime createdAt;

    public PostResponseDTO(UUID postId, String title, String body, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }

    public PostResponseDTO(PostModel post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.createdAt = post.getLocalDateTime();
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
