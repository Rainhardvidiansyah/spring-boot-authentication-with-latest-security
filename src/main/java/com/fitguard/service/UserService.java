package com.fitguard.service;


import com.fitguard.entity.AppUser;
import com.fitguard.entity.ERoleNames;
import com.fitguard.entity.Role;
import com.fitguard.repository.RolesRepository;
import com.fitguard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private List<Role> getRoleUser(){

        Role role = rolesRepository.findRoleByERoleNames(ERoleNames.ROLE_USER)
                .orElseThrow(() -> new RuntimeException(""));

        log.info("ROLE IN USER SERVICE USING PRIVATE METHOD FIND ROLE: {}", role.getERoleNames());

        List<Role> roles = new ArrayList<>();

        roles.add(role);

        return roles;
    }


    public AppUser registration(AppUser user){
        if(user.getEmail().isEmpty()){
            System.out.println("email kosong");
            throw new RuntimeException("Email is empty");
        } if (user.getPassword().isEmpty()) {
            System.out.println("password kosong");
            throw new RuntimeException("Password is empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getRoleUser());
        return userRepository.save(user);
    }

    public AppUser findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
    }

    public boolean findExistingUser(String email){
        final boolean user = userRepository.existsByEmail(email);
        return false; //if it exists what are you going to do?
    }


}
