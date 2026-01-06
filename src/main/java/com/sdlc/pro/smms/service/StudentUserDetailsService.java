package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.AppUserDetails;
import com.sdlc.pro.smms.entity.AppUser;
import com.sdlc.pro.smms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("studentUserDetailsService")
@RequiredArgsConstructor
public class StudentUserDetailsService implements UserDetailsService {
    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = studentRepository.findByEmail(email)
                .map(u -> (AppUser) u)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AppUserDetails(user);
    }
}
