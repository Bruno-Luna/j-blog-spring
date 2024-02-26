package br.com.blog.services;

import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    BCryptPasswordEncoder crypt = new BCryptPasswordEncoder();

    @Transactional
    public UserModel saveUser(UserModel userModel) {
        userModel.setPassword(crypt.encode(userModel.getPassword()));
        return userRepository.save(userModel);
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

}
