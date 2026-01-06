package com.sdlc.pro.smms.util;

import com.sdlc.pro.smms.dto.AppUserDetails;
import com.sdlc.pro.smms.entity.AppUser;
import com.sdlc.pro.smms.entity.Student;
import com.sdlc.pro.smms.enums.Semester;
import com.sdlc.pro.smms.exception.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {

    private static AppUser currentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUserDetails userDetails) {
            return userDetails.getAppUser();
        }

        throw new UserNotFoundException("Logged in user not found!");
    }

    public static Integer loggedInUserId() {
        return currentUser().getId();
    }

    public static Semester getCurrentSemester() {
        var user = currentUser();
        if (user instanceof Student student) {
            return student.getSemester();
        }

        throw new IllegalStateException("The current logged in user is not student type");
    }
}
