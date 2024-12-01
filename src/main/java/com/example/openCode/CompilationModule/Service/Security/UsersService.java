package com.example.openCode.CompilationModule.Service.Security;

import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private UsersRepo usersRepo;
    private AuthenticationManager authManager;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private JwtService jwtService;


    @Autowired
    public UsersService(UsersRepo usersRepo, AuthenticationManager authManager, JwtService jwtService) {
        this.usersRepo = usersRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return usersRepo.save(user);
    }

    public String login(Users user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(auth.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return "Failed";
    }

}
