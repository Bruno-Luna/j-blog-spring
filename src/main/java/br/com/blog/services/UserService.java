package br.com.blog.services;

import br.com.blog.dto.UserRequestDTO;
import br.com.blog.dto.UserResponseDTO;
import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {

        UserModel userModel = new UserModel();
        userModel.setUsername(userRequestDTO.getUsername());
        userModel.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userRepository.save(userModel);
        return new UserResponseDTO(userModel.getUserId(), userModel.getUsername(), userModel.getLocalDateTime());
    }

    public Optional<UserModel> existsUsername(UserRequestDTO userRequestDTO) {
        return userRepository.findByUsername(userRequestDTO.getUsername());
    }

    public UserModel verifyUsername(UserRequestDTO userRequestDTO) {
        return userRepository.getByUsername(userRequestDTO.getUsername());
    }

    public Boolean checkPassword(String passwordEntered, String currentPassword) {
        return passwordEncoder.matches(passwordEntered, currentPassword);
    }

}
