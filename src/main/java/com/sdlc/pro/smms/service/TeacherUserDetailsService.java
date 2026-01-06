package com.sdlc.pro.smms.service;

import com.sdlc.pro.smms.dto.AppUserDetails;
import com.sdlc.pro.smms.entity.AppUser;
import com.sdlc.pro.smms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("teacherUserDetailsService")
@RequiredArgsConstructor
public class TeacherUserDetailsService implements UserDetailsService {
    private final TeacherRepository teacherRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = teacherRepository.findByEmail(email)
                .map(u -> (AppUser) u)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AppUserDetails(user);
    }
}
