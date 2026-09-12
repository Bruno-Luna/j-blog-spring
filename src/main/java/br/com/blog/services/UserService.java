package br.com.blog.services;

import br.com.blog.dto.UserResponseDTO;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    BCryptPasswordEncoder crypt = new BCryptPasswordEncoder();

    @Transactional
    public UserResponseDTO saveUser(UserModel userModel) {
        userModel.setPassword(crypt.encode(userModel.getPassword()));
        userModel.setLocalDateTime(LocalDateTime.now());
        userRepository.save(userModel);
        return new UserResponseDTO(userModel.getUserId(), userModel.getUsername(), userModel.getLocalDateTime());
    }

    public Optional<UserModel> existsUsername(UserModel userModel) {
        return userRepository.findByUsername(userModel.getUsername());
    }

    public UserModel verifyUsername(UserModel userModel) {
        return userRepository.getByUsername(userModel.getUsername());
    }

    public Boolean checkPassword(String passwordEntered, String currentPassword) {
        return crypt.matches(passwordEntered, currentPassword);
    }

    public HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(
                    auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + encodedAuth;
            set("Authorization", authHeader);
        }};
    }
}
