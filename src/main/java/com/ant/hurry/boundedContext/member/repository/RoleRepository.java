package com.ant.hurry.boundedContext.member.repository;

import com.ant.hurry.boundedContext.member.entity.Role;
import com.ant.hurry.boundedContext.member.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType roleName);
}
