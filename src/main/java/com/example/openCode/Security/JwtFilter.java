package com.example.openCode.Security;


import com.example.openCode.CompilationModule.Controller.TaskController;
import com.example.openCode.CompilationModule.Service.UserSecurity.JwtService;
import com.example.openCode.CompilationModule.Service.UserSecurity.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationContext context;
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final String TOKEN_PREFIX = "Bearer ";
    private final String TOKEN_HEADER = "Authorization";
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public JwtFilter(JwtService jwtService, ApplicationContext context, MyUserDetailsService myUserDetailsService) {
        this.jwtService = jwtService;
        this.context = context;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(TOKEN_HEADER);
        String token = null;
        String username = null;
        log.atInfo().log("Authorization header: " + authHeader); //logowanie!

        //BufferedRequestWrapper wrappedRequest = new BufferedRequestWrapper(request);

        // Odczytanie ciała żądania
        //String body = wrappedRequest.getBody();

        //  log.atError().log("Body: " + body);


        if(authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(TOKEN_PREFIX.length());
            username = jwtService.extractUserName(token);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
                    //context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        log.info("SecurityContext Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }


}
