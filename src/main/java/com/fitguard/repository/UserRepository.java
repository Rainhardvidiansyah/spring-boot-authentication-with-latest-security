package com.fitguard.repository;

import com.fitguard.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    public Optional<AppUser> findAppUserByEmail(String email);

    public boolean existsByEmail(String email);
}
