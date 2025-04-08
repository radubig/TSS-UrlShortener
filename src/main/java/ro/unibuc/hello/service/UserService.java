package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.exception.NoPermissionException;
import ro.unibuc.hello.exception.UserAlreadyExistsException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity registerUser(User newUser)
    {
        String username = newUser.getUsername();
        String email = newUser.getEmail();
        String password = newUser.getPassword();
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(UserAlreadyExistsException.SameCredentials.USERNAME);
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(UserAlreadyExistsException.SameCredentials.EMAIL);
        }

        String hashedPassword = passwordEncoder.encode(password);
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(String username, String userId) {
        UserEntity user = userRepository.findByUsername(username);

        if (user != null) {
            if(!user.getId().equals(userId)){
                throw new NoPermissionException("You are not allowed to delete this user");
            }

            userRepository.delete(user);
        } else {
            throw new EntityNotFoundException(username);
        }
    }
}
