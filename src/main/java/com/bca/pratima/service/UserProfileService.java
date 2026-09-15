package com.bca.pratima.service;

import com.bca.pratima.dto.UserProfile;
import com.bca.pratima.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserProfileService {

    UserProfile getUserProfile(Integer id);

    User createOrUpdateUserProfile(User user, MultipartFile imageFile);

    User findById(Integer id);
}
