package br.com.blog.services;

import br.com.blog.models.UserModel;
import br.com.blog.repositories.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.Charset;
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

    public HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
}
