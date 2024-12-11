package com.example.openCode.CompilationModule.Service.UserSecurity;

import com.example.openCode.CompilationModule.Model.Users.UserPrincipal;
import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Repository.UsersRepo;
import com.example.openCode.Security.JwtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepo repo;
    private static final Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.atInfo().log("Attempting to load user: " + username);

        Users user = repo.findByUsername(username);

        if(user == null) {
            log.atWarn().log("User not found: " + username);
            throw new UsernameNotFoundException("User not found");
        }

        return new UserPrincipal(user);
    }
}
