package com.bca.pratima.controller;

import com.bca.pratima.dto.Response;
import com.bca.pratima.dto.Status;
import com.bca.pratima.dto.UserProfile;
import com.bca.pratima.entity.User;
import com.bca.pratima.exception.ResourceNotFoundException;
import com.bca.pratima.mapper.UserProfileMapper;
import com.bca.pratima.service.UserProfileService;
import com.bca.pratima.utils.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("profile")
public class ProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private EmailService emailService;

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUserProfile(@PathVariable Integer id) {
        UserProfile data = userProfileService.getUserProfile(id);
        Status status = new Status();
        status.setStatus(HttpStatus.OK.value());
        status.setMessage("User Profile retrieved.");

        Response response = new Response();
        response.setStatus(status);
        response.setData(data);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    //@PreAuthorize("hasAnyRole('NORMAL', 'ADMIN')")
    @PostMapping(value = "/create-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> updateUserProfile(@RequestPart("userProfile") @Valid UserProfile userProfile,
                                                      @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        User user = userProfileService.findById(userProfile.getId());
        if (user==null) {
            throw new ResourceNotFoundException("User Not Exist.");
        }
        user = userProfileMapper.userProfileToUser(userProfile, user);
        user = userProfileService.createOrUpdateUserProfile(user,imageFile);
        Status status = new Status();
        status.setStatus(HttpStatus.CREATED.value());
        status.setMessage("Profile has been updated.");
        Response response = new Response();
        response.setStatus(status);
        try{
            emailService.sendEmail(
                    user.getEmail(),
                    "Book Social Network - Your Profile Has Been Updated",
                    "Hello " + user.getName() + ",\n\n"
                            + "Your FindMyVehicle profile has been successfully updated.\n\n"
                            + "If you made this change, no further action is required.\n\n"
                            + "If you did not make this change, please log in to your FindMyVehicle account "
                            + "and secure your account immediately.\n"
            );
        } catch (Exception e) {

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
