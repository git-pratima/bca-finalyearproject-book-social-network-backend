package com.bca.pratima.mapper;

import com.bca.pratima.dto.AddressDto;
import com.bca.pratima.entity.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressMapper {

    public Address toAddress(AddressDto bookAddress) {
        Address address = Address.builder()
                .addressLine1(bookAddress.getAddressLine1())
                .addressLine2(bookAddress.getAddressLine2())
                .city(bookAddress.getCity())
                .state(bookAddress.getState())
                .country(bookAddress.getCountry())
                .landmark(bookAddress.getLandmark())
                .postalCode(bookAddress.getPin()).build();
        return address;
    }

    public void updateAddress(Address address, AddressDto bookAddress) {
        address.setAddressLine1(bookAddress.getAddressLine1());
        address.setAddressLine2(bookAddress.getAddressLine2());
        address.setCity(bookAddress.getCity());
        address.setState(bookAddress.getState());
        address.setCountry(bookAddress.getCountry());
        address.setLandmark(bookAddress.getLandmark());
        address.setPostalCode(bookAddress.getPin());
    }
}
