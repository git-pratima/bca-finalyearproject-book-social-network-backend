package com.bca.pratima.service;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.dto.*;
import com.bca.pratima.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public interface BookService {

    public Integer save(BookRequest request, Authentication connectedUser);

    public BookResponse findById(Integer bookId);

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser,String searchParameter,String city,String state,String postalCode,String searchKeyword);

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser, String searchParameter,String searchKeyword);

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser);

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser);

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId);

    //
    BookBorrowResponseDto createBorrowRequest(@Valid BookBorrowRequestDto request, Authentication connectedUser);

    PageResponse<BookBorrowResponseDto> findSubmittedUserBookBorrowRequest(int page, int size, Authentication connectedUser,BookBorrowStatus status,String searchParameter,String searchKeyword);

    PageResponse<BookBorrowResponseDto> findBorrowedBooks(int page, int size, Authentication connectedUser,BookBorrowStatus status,String searchParameter,String searchKeyword);

    Long countSharedBookByUser(boolean archived,boolean shareable,User connectedUser);

    Long countBorrowedBooksByUser(User connectedUser, BookBorrowStatus status);

    String updateBorrowRequestStatus(@Valid UpdateBookBorrowRequest request, Authentication connectedUser);

    PageResponse<BookBorrowResponseDto> findUserReturnedBooks(int page, int size, Authentication connectedUser, BookBorrowStatus bookBorrowStatus, String searchParameter, String searchKeyword);
}
