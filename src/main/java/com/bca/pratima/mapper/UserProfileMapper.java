package com.bca.pratima.mapper;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.dto.AddressDto;
import com.bca.pratima.dto.UserProfile;
import com.bca.pratima.entity.Address;
import com.bca.pratima.entity.User;
import com.bca.pratima.repository.BookBorrowRepository;
import com.bca.pratima.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileMapper {

    @Autowired
    private BookService bookService;

    public User userProfileToUser(UserProfile userProfile, User user) {
        Address address = user.getAddress();
        if(address==null){
            address = new Address();
        }
        if(userProfile.getEmail()!=null){
           // user.setEmail(userProfile.getEmail());  Do not allow user to chnage Email Id
        }
        if(userProfile.getFirstName()!=null && !userProfile.getFirstName().trim().isEmpty()){
            user.setFirstname(userProfile.getFirstName().trim());
        }
        if(userProfile.getLastName()!=null && !userProfile.getLastName().trim().isEmpty()){
            user.setLastname(userProfile.getLastName().trim());
        }

        AddressDto addressDto = userProfile.getAddress();
        if(addressDto.getAddressLine1()!=null && !addressDto.getAddressLine1().trim().isEmpty()){
            address.setAddressLine1(addressDto.getAddressLine1().trim());
        }
        if(addressDto.getAddressLine2()!=null && !addressDto.getAddressLine2().trim().isEmpty()){
            address.setAddressLine2(addressDto.getAddressLine2().trim());
        }
        if(addressDto.getCity()!=null && !addressDto.getCity().trim().isEmpty()){
            address.setCity(addressDto.getCity().trim());
        }
        if(addressDto.getState()!=null && !addressDto.getState().trim().isEmpty()){
            address.setState(addressDto.getState().trim());
        }
        if(addressDto.getLandmark()!=null && !addressDto.getLandmark().trim().isEmpty()){
            address.setLandmark(addressDto.getLandmark().trim());
        }
        if(addressDto.getPin()!=null && !addressDto.getPin().trim().isEmpty()){
            address.setPostalCode(addressDto.getPin().trim());
        }
        if(addressDto.getCountry()!=null && !addressDto.getCountry().trim().isEmpty()){
            address.setCountry(addressDto.getCountry().trim());
        }
        user.setAddress(address);
        return user;
    }

    public UserProfile userToUserProfile(User user) {
        Address address = user.getAddress();
        UserProfile userProfile = new UserProfile();
        userProfile.setId(user.getId());
        userProfile.setFirstName(user.getFirstname());
        userProfile.setLastName(user.getLastname());
        userProfile.setEmail(user.getEmail());
        userProfile.setMemberSince(user.getCreatedDate());
        userProfile.setNumberOfBooksShared(bookService.countSharedBookByUser(false,true,user));
        userProfile.setNumberOfBooksBorrowed(bookService.countBorrowedBooksByUser(user, BookBorrowStatus.APPROVED));
        if(address!=null){
            userProfile.getAddress().setId(address.getId());
            userProfile.getAddress().setAddressLine1(address.getAddressLine1());
            userProfile.getAddress().setAddressLine2(address.getAddressLine2());
            userProfile.getAddress().setCity(address.getCity());
            userProfile.getAddress().setCountry(address.getCountry());
            userProfile.getAddress().setState(address.getState());
            userProfile.getAddress().setPin(address.getPostalCode());
            userProfile.getAddress().setLandmark(address.getLandmark());
        }
        return userProfile;
    }
}
