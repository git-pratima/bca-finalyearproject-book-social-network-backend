package com.bca.pratima.mapper;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.dto.*;
import com.bca.pratima.entity.*;
import com.bca.pratima.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookMapper {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

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

    public BookBorrowRequest toBookBorrowRequest(BookBorrowRequestDto request,Book book, Authentication connectedUser) {
        User borrower = (User) connectedUser.getPrincipal();

        Optional<User> bookOwnerOptional = userRepository.findById(book.getOwner().getId());
        User bookOwner=null;
        if(bookOwnerOptional.isPresent()){
            bookOwner = bookOwnerOptional.get();
        }

        return BookBorrowRequest.builder()
                .requestDate(new Date())
                .borrowToDate(request.getBorrowToDate())
                .borrowFromDate(request.getBorrowFromDate())
                .returnPeriodDay(request.getReturnPeriodDay())
                .finalReturnDate(request.getFinalReturnDate())
                .status(BookBorrowStatus.SUBMITTED)
                .agreeToFollowPickupInstruction(request.getAgreeToFollowPickupInstruction())
                .agreeToReturnBorrowedBookAtSameLocation(request.getAgreeToReturnBorrowedBookAtSameLocation())
                .comment(request.getComment())
                .book(book)
                .borrower(borrower)
                .bookOwner(bookOwner).build();
    }

    public BookBorrowResponseDto toBookBorrowResponse(BookBorrowRequest savedBookBorrowRequest) {
        BookBorrowResponseDto bookBorrowResponseDto = BookBorrowResponseDto.builder()
                .bookId(savedBookBorrowRequest.getBook().getId())
                .bookName(savedBookBorrowRequest.getBook().getTitle())
                .borrowRequestId(savedBookBorrowRequest.getId())
                .bookCover(savedBookBorrowRequest.getBook().getBookCover())
                .borrowToDate(savedBookBorrowRequest.getBorrowToDate())
                .borrowFromDate(savedBookBorrowRequest.getBorrowFromDate())
                .comment(savedBookBorrowRequest.getComment())
                .returnPeriodDay(savedBookBorrowRequest.getReturnPeriodDay())
                .agreeToReturnBorrowedBookAtSameLocation(savedBookBorrowRequest.getAgreeToReturnBorrowedBookAtSameLocation())
                .agreeToFollowPickupInstruction(savedBookBorrowRequest.getAgreeToFollowPickupInstruction())
                .finalReturnDate(savedBookBorrowRequest.getFinalReturnDate())
                .status(savedBookBorrowRequest.getStatus())
                .build();

        return bookBorrowResponseDto;
    }

    public List<BookBorrowResponseDto> toBookBorrowResponseDto(List<BookBorrowRequest> bookBorrowRequestList) {

        List<BookBorrowResponseDto> list = new ArrayList<BookBorrowResponseDto>();

        for(BookBorrowRequest bookBorrowRequest : bookBorrowRequestList){
            BookBorrowResponseDto bookBorrowResponseDto = BookBorrowResponseDto.builder()
                    .bookId(bookBorrowRequest.getBook().getId())
                    .bookName(bookBorrowRequest.getBook().getTitle())
                    .borrowRequestId(bookBorrowRequest.getId())
                    .bookCover(bookBorrowRequest.getBook().getBookCover())
                    .borrowToDate(bookBorrowRequest.getBorrowToDate())
                    .borrowFromDate(bookBorrowRequest.getBorrowFromDate())
                    .comment(bookBorrowRequest.getComment())
                    .returnPeriodDay(bookBorrowRequest.getReturnPeriodDay())
                    .agreeToReturnBorrowedBookAtSameLocation(bookBorrowRequest.getAgreeToReturnBorrowedBookAtSameLocation())
                    .agreeToFollowPickupInstruction(bookBorrowRequest.getAgreeToFollowPickupInstruction())
                    .finalReturnDate(bookBorrowRequest.getFinalReturnDate())
                    .status(bookBorrowRequest.getStatus())
                    .build();

            list.add(bookBorrowResponseDto);
        }
        return list;
    }
}
