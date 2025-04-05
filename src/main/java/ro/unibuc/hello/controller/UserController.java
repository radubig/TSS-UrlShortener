package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerUser(@RequestBody User user) {
        try {
            UserEntity newEntity = userService.registerUser(user);
            return ResponseEntity.ok(newEntity);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
