package com.bca.pratima.mapper;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.dto.*;
import com.bca.pratima.entity.*;
import com.bca.pratima.repository.UserRepository;
import com.bca.pratima.utils.UserUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookMapper {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtils userUtils;

    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.getId())
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .authorName(request.getAuthorName())
                .synopsis(request.getSynopsis())
                .archived(request.isArchive())
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
        List<Feedback> feedbackList = null;
        Double averageRating = 0.0;
        if(book.getFeedbacks()!=null){
            feedbackList = book.getFeedbacks();

          feedbackList = book.getFeedbacks() == null
                    ? List.of()
                    : book.getFeedbacks().stream()
                    .sorted(Comparator.comparing(
                            Feedback::getId,
                            Comparator.reverseOrder()
                    ))
                    .limit(5)
                    .toList();


            averageRating = feedbackList == null || feedbackList.isEmpty() || feedbackList.size()==0
                    ? 0.0
                    : Math.round(
                    feedbackList.stream()
                            .mapToDouble(Feedback::getRating)
                            .average()
                            .orElse(0.0) * 10
            ) / 10.0;
        }

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                .bookAddress(addressDto)
                .pickupInstructions(book.getPickupInstructions())
                .pickUpLocation(book.getPickUpLocation())
                // Cloudinary stores a remote delivery URL, not a local file path.
                // Returning it as-is also keeps books without covers null-safe.
                .cover(book.getBookCover())
                .feedbackList(feedbackList)
                .averageRating(averageRating)
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
                    .author(bookBorrowRequest.getBook().getAuthorName())
                    .borrowToDate(bookBorrowRequest.getBorrowToDate())
                    .borrowFromDate(bookBorrowRequest.getBorrowFromDate())
                    .comment(bookBorrowRequest.getComment())
                    .returnPeriodDay(bookBorrowRequest.getReturnPeriodDay())
                    .agreeToReturnBorrowedBookAtSameLocation(bookBorrowRequest.getAgreeToReturnBorrowedBookAtSameLocation())
                    .agreeToFollowPickupInstruction(bookBorrowRequest.getAgreeToFollowPickupInstruction())
                    .finalReturnDate(bookBorrowRequest.getFinalReturnDate())
                    .status(bookBorrowRequest.getStatus())
                    .borrowerName(userUtils.getUserNameByUserId(bookBorrowRequest.getBorrower().getId()))
                    .ownerName(userUtils.getUserNameByUserId(bookBorrowRequest.getBookOwner().getId()))
                    .build();

            list.add(bookBorrowResponseDto);
        }
        return list;
    }
}
