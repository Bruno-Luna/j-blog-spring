package br.com.blog.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "TB_POST")
public class PostModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID postId;

    @Column(nullable = false)
    @Size(max = 100)
    private String title;
    @Column(nullable = false)
    @Lob
    private String body;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-YYYY")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties({"myPost"})
    private UserModel user;

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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
