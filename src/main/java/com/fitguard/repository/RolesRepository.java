package com.fitguard.repository;

import com.fitguard.entity.Role;
import com.fitguard.entity.ERoleNames;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findRoleByERoleNames(ERoleNames ERoleNames);
}
