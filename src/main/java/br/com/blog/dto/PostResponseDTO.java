package br.com.blog.dto;

import br.com.blog.models.PostModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class PostResponseDTO {

    private String title;

    private String body;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt_BR")
    private LocalDateTime createdAt;

    private UUID userId;

    private String username;

    public PostResponseDTO(String title, String body, LocalDateTime createdAt, UUID userId, String username) {
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
    }

    public PostResponseDTO(PostModel post) {
        this.title = post.getTitle();
        this.body = post.getBody();
        this.createdAt = post.getLocalDateTime();
        this.userId = post.getUser().getUserId();
        this.username = post.getUser().getUsername();
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
