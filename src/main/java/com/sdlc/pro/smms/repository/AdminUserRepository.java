package com.sdlc.pro.smms.repository;

import com.sdlc.pro.smms.entity.AdminUser;
import com.sdlc.pro.smms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {
    Optional<AdminUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
