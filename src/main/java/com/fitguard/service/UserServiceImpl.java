package com.fitguard.service;

import com.fitguard.entity.AppUser;
import com.fitguard.entity.UserDetailsImpl;
import com.fitguard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<AppUser> user = userRepository.findAppUserByEmail(email);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not Found");
        }
        return UserDetailsImpl.createUser(user.get());
    }
}
