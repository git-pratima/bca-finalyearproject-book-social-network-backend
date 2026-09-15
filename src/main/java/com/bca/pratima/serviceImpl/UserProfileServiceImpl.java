package com.bca.pratima.serviceImpl;

import com.bca.pratima.dto.UserProfile;
import com.bca.pratima.entity.User;
import com.bca.pratima.exception.ResourceNotFoundException;
import com.bca.pratima.mapper.UserProfileMapper;
import com.bca.pratima.repository.UserRepository;
import com.bca.pratima.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public UserProfile getUserProfile(Integer id) {
        Optional<User> user = userRepository.findById(id);
        UserProfile userProfile = null;
        if(user.isPresent()){
            userProfile = userProfileMapper.userToUserProfile(user.get());
        }

        return userProfile;
    }

    @Override
    public User createOrUpdateUserProfile(User user, MultipartFile imageFile) {
        //Save Image
        user = userRepository.save(user);

        return user;
    }

    @Override
    public User findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not Found."));
        return user;
    }
}
