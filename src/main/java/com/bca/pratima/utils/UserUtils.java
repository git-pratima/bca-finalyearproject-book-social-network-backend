package com.bca.pratima.utils;

import com.bca.pratima.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserUtils {

    public User getLoggedInUser(Authentication connectedUser){
        User loggedInUser = (User) connectedUser.getPrincipal();
        return loggedInUser;
    }

}
