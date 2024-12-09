package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Service.UserSecurity.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsersController {

    private UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService){
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users users){
        Map<String, String> response = new HashMap<>();

        if(usersService.isUsernameTaken(users.getUsername())){
            response.put("message","Username is already in use");
            response.put("username",users.getUsername());
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);

        }
        Users returnUser = usersService.register(users);
        response.put("message","User" + returnUser.getUsername() + " created successfully");
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users users){
        return usersService.login(users);
    }



}
