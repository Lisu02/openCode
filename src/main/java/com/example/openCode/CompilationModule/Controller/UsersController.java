package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.UsersDTO;
import com.example.openCode.CompilationModule.Model.Users.UserPrincipal;
import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Service.UserSecurity.MyUserDetailsService;
import com.example.openCode.CompilationModule.Service.UserSecurity.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UsersController {

    private UsersService usersService;
    private MyUserDetailsService myUserDetailsService;
    private AuthenticationManager authenticationManager;
    private SecurityContextHolderStrategy securityContextHolderStrategy;
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);


    @Autowired
    public UsersController(UsersService usersService,
                           MyUserDetailsService myUserDetailsService,
                           AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.myUserDetailsService = myUserDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users users){
        Map<String, String> response = new HashMap<>();

        if(usersService.isUsernameTaken(users.getUsername())){
            response.put("message","Username is already in use");
            response.put("username",users.getUsername());
            log.atWarn().log("Username is already in use");
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);

        }
        Users returnUser = usersService.register(users);
        response.put("message","User" + returnUser.getUsername() + " created successfully");
        log.atInfo().log("User: " + returnUser.getUsername() + " created successfully");
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

//    private SecurityContextRepository securityContextRepository =
//            new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users users,HttpServletRequest request,HttpServletResponse response){
        Map<String,String> responseMap = new HashMap<>();
        return usersService.login(users,responseMap);
    }

    @GetMapping("/userInfo/{id}")
    public ResponseEntity<?> getUsername(@PathVariable int id){
        Map<String, String> response = new HashMap<>();

        String username = usersService.getUsernameFromId(id);
        if (username != null) {
            response.put("username",username);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.put("username","User not found");
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @GetMapping("/userInfo/my")
    public ResponseEntity<?> getOwnUserInfo(){

        // TODO: REFACTOR GETTING USER DATA FROM TOKEN TO SERVICE LOGIC
        // Pobranie nazwy użytkownika z kontekstu bezpieczeństwa (tokenu)
        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Pobranie użytkownika z MyUserDetailsService na podstawie nazwy użytkownika
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username); // Wykorzystanie loadUserByUsername

        // Uzyskanie obiektu Users z UserDetails
        Users user = ((UserPrincipal) userDetails).getUser();  // Rzutowanie na UserPrincipal i uzyskanie obiektu Users
        UsersDTO usersDTO = usersService.mapUsersToUsersDTO(user);

        if(user != null){
            return new ResponseEntity<>(usersDTO,HttpStatus.OK);
        }else{
            log.atError().log("Token recived from user didn't provide enough info to find user [token] -> user NOT FOUND");
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
    }



}
