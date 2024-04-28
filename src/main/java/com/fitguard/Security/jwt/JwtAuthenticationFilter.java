package com.fitguard.Security.jwt;

import com.fitguard.service.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    private UserServiceImpl userServiceImpl;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {



        String userEmail = "";
        final String authHeader = request.getHeader("Authorization");

        String jwt = null;


        if(authHeader != null && authHeader.startsWith("Bearer ")){
            //userEmail = jwtService.extractUserName(jwt); // extract userEmail data from jwt service
            jwt = authHeader.substring(7);
            LOGGER.info("Data jwt in auth filter class: {} ", jwt);
            userEmail = jwtService.extractUserName(jwt); //seems it is not working!
            LOGGER.info("UserEmail in auth filter class: userEmail {} ", userEmail);
            return;
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userServiceImpl.loadUserByUsername(userEmail);
            LOGGER.info("User email in auth filter class: {}", userDetails.getUsername());

            String userName = jwtService.getUserNameFromJwtToken(userDetails.getUsername());
            LOGGER.info("USERNAME in auth filter class: {}", userName);

            if(jwtService.isTokenValid(userEmail, userDetails)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
