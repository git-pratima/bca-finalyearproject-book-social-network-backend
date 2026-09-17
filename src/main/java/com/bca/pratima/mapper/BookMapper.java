package com.bca.pratima.mapper;

import com.bca.pratima.dto.AddressDto;
import com.bca.pratima.dto.BookRequest;
import com.bca.pratima.dto.BookResponse;
import com.bca.pratima.dto.BorrowedBookResponse;
import com.bca.pratima.entity.Address;
import com.bca.pratima.entity.Book;
import com.bca.pratima.entity.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.getId())
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .authorName(request.getAuthorName())
                .synopsis(request.getSynopsis())
                .archived(false)
                .shareable(request.isShareable())
                .pickUpLocation(request.getPickUpLocation())
                .pickupInstructions(request.getPickupInstructions())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        AddressDto addressDto = new AddressDto();
        Address dbAddress = null;
        if(book.getAddress()!=null){
            dbAddress = book.getAddress();
            addressDto.setId(dbAddress.getId());
            addressDto.setAddressLine1(dbAddress.getAddressLine1());
            addressDto.setAddressLine2(dbAddress.getAddressLine2());
            addressDto.setState(dbAddress.getState());
            addressDto.setCountry(dbAddress.getCountry());
            addressDto.setCity(dbAddress.getCity());
            addressDto.setLandmark(dbAddress.getLandmark());
            addressDto.setPin(dbAddress.getPostalCode());
        }
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                .bookAddress(addressDto)
                .pickupInstructions(book.getPickupInstructions())
                .pickUpLocation(book.getPickUpLocation())
                // Cloudinary stores a remote delivery URL, not a local file path.
                // Returning it as-is also keeps books without covers null-safe.
                .cover(book.getBookCover())
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
