package com.bca.pratima.utils;

import com.bca.pratima.entity.User;
import com.bca.pratima.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserUtils {

    @Autowired
    private UserRepository userRepository;

    public User getLoggedInUser(Authentication connectedUser){
        User loggedInUser = (User) connectedUser.getPrincipal();
        return loggedInUser;
    }

    public String getUserNameByUserId(Integer id){
        User user =  userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        "No User found with ID:: " + id
                )
        );;
        return user.getFirstname()+" "+user.getLastname();
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No user is currently authenticated");
        }
        return (User) authentication.getPrincipal();
    }


}
