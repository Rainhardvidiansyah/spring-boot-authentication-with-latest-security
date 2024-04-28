package com.fitguard.dto;

import com.fitguard.entity.AppUser;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class AppUserDto {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    public static AppUser toUser(AppUserDto userDto){
        var user = new AppUser();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }
}
