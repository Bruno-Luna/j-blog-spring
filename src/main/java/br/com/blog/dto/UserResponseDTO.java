package br.com.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponseDTO {

    private UUID userId;

    private String username;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt_BR")
    private LocalDateTime localDateTime;

    public UserResponseDTO(UUID userId, String username, LocalDateTime localDateTime) {
        this.userId = userId;
        this.username = username;
        this.localDateTime = localDateTime;
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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
