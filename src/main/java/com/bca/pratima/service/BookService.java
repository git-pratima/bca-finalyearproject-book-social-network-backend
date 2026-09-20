package com.bca.pratima.service;

import com.bca.pratima.dto.*;
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

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser);

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser);

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser);

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser);

    public Integer borrowBook(Integer bookId, Authentication connectedUser);

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser);

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser);

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId);

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser);

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser);

    BookBorrowResponseDto createBorrowRequest(@Valid BookBorrowRequestDto request, Authentication connectedUser);
}
