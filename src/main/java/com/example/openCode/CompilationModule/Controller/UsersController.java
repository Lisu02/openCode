package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Service.UserSecurity.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    private UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService){
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public Users register(@RequestBody Users users){
        return usersService.register(users);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users users){
        return usersService.login(users);
    }

}
