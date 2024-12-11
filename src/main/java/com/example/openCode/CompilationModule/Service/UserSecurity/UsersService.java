package com.example.openCode.CompilationModule.Service.UserSecurity;

import com.example.openCode.CompilationModule.DTO.UsersDTO;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public boolean isUsernameTaken(String username) {
        return usersRepo.findByUsername(username) != null;
    }

    public String getUsernameFromId(int id) {
        Optional<Users> users = usersRepo.findById(id);
        if(users.isPresent()) {
            return users.get().getUsername();
        }else {
            return null;
        }

    }

    public Users getMyOwnUserInfo(String token){
        return null; //TODO: Refactor getting userInfo from token
    }

    //Cannot register a user that already exists AO.
    public Users register(Users user) {
        Users userFromDB = usersRepo.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return null;
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return usersRepo.save(user);
    }

    private SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public String login(Users user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(auth.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return "Failed";
    }

    //added new prod

    public UsersDTO mapUsersToUsersDTO(Users users) {
        return UsersDTO.builder()
                .id(users.getId())
                .username(users.getUsername())
                .userSolutionsId(mapUserSolutionsToIdOnly(users.getUserSolutions()))
                .taskListId(mapUserCreatedTasksToIdOnly(users.getTaskList()))
                .build();

    }

    private List<Long> mapUserSolutionsToIdOnly(List<UserSolution> userSolutions) {
        List<Long> ids = new ArrayList<>();
        for (UserSolution userSolution : userSolutions) {
            ids.add(userSolution.getId());
        }
        return ids;
    }

    private List<Long> mapUserCreatedTasksToIdOnly(List<Task> userCreatedTasks) {
        List<Long> ids = new ArrayList<>();
        for (Task userCreatedTask : userCreatedTasks) {
            ids.add(userCreatedTask.getId());
        }
        return ids;
    }
}
