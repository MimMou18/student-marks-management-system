package com.sdlc.pro.smms.entity;

import com.sdlc.pro.smms.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String resetPasswordSecret;

    @Enumerated(EnumType.STRING)
    private Role role;
}
