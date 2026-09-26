package com.bca.pratima.serviceImpl;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.dto.*;
import com.bca.pratima.entity.*;
import com.bca.pratima.exception.AccessDeniedException;
import com.bca.pratima.exception.OperationNotPermittedException;
import com.bca.pratima.mapper.AddressMapper;
import com.bca.pratima.mapper.BookMapper;
import com.bca.pratima.mapper.FeedbackMapper;
import com.bca.pratima.repository.BookBorrowRepository;
import com.bca.pratima.repository.BookRepository;
import com.bca.pratima.repository.FeedBackRepository;
import com.bca.pratima.service.BookService;
import com.bca.pratima.service.FileStorageService;
import com.bca.pratima.utils.CloudinaryImageUtils;
import com.bca.pratima.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

//import static com.alibou.book.book.BookSpecification.withOwnerId;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CloudinaryImageUtils cloudinaryImageUtils;

    @Autowired
    private BookBorrowRepository bookBorrowRepository;

    @Autowired
    private FeedBackRepository feedbackRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FeedbackMapper feebackMapper;

    @Override
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book;

        if (request.getId() != null) {
            book = bookRepository.findById(request.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.getId()));
            if (!Objects.equals(book.getOwner().getId(), user.getId())) {
                throw new OperationNotPermittedException("You cannot update another user's book");
            }

            book.setTitle(request.getTitle());
            book.setIsbn(request.getIsbn());
            book.setAuthorName(request.getAuthorName());
            book.setSynopsis(request.getSynopsis());
            book.setShareable(request.isShareable());
            book.setPickUpLocation(request.getPickUpLocation());
            book.setPickupInstructions(request.getPickupInstructions());
            book.setArchived(request.isArchive());
        } else {
            book = bookMapper.toBook(request);
            book.setOwner(user);
        }

        if (request.getBookAddress() != null) {
            Address address = book.getAddress();
            if (address == null) {
                book.setAddress(addressMapper.toAddress(request.getBookAddress()));
            } else {
                addressMapper.updateAddress(address, request.getBookAddress());
            }
        }

        return bookRepository.save(book).getId();
    }
    @Override
    public BookResponse findById(Integer bookId) {
        //Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));


        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
    }
    @Override
    public PageResponse<BookResponse> findAllBooks(
            int page,
            int size,
            Authentication connectedUser,
            String searchParameter,
            String city,
            String state,
            String postalCode,
            String searchKeyword
    ) {

        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );

        // Handle empty values
        if (searchParameter != null && searchParameter.isBlank()) {
            searchParameter = null;
        }

        if (city != null && city.isBlank()) {
            city = null;
        }

        if (state != null && state.isBlank()) {
            state = null;
        }

        if (postalCode != null && postalCode.isBlank()) {
            postalCode = null;
        }

        if (searchKeyword != null && searchKeyword.isBlank()) {
            searchKeyword = null;
        }

        Page<Book> books = bookRepository.findAllDisplayableBooks(
                pageable,
                user.getId(),
                searchParameter,
                city,
                state,
                postalCode,
                searchKeyword
        );

        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(
            int page,
            int size,
            Authentication connectedUser,
            String searchParameter,
            String searchKeyword
    ) {
        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending()
        );

        if (searchParameter != null && searchParameter.isBlank()) {
            searchParameter = null;
        }

        if (searchKeyword != null && searchKeyword.isBlank()) {
            searchKeyword = null;
        }

        Page<Book> books = bookRepository.findAllBooksByOwner(
                pageable,
                user.getId(),
                searchParameter,
                searchKeyword
        );

        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    @Override
    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }
    @Override
    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public void uploadBookCoverPicture(
            MultipartFile file,
            Authentication connectedUser,
            Integer bookId
    ) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "No book found with ID:: " + bookId
                        )
                );

        User user = (User) connectedUser.getPrincipal();

        // Make sure only the owner can upload/change the cover
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to change this book cover"
            );
        }

        try {
            String imageUrl =
                    cloudinaryImageUtils.uploadImage(file, bookId);

            book.setBookCover(imageUrl);

            bookRepository.save(book);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload book cover to Cloudinary",
                    e
            );
        }
    }

    @Override
    public PageResponse<BookBorrowResponseDto> findSubmittedUserBookBorrowRequest(
            int page,
            int size,
            Authentication connectedUser,
            BookBorrowStatus status,
            String searchParameter,
            String searchKeyword
    ) {

        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("requestDate").descending()
        );

        if (searchParameter != null && searchParameter.isBlank()) {
            searchParameter = null;
        }

        if (searchKeyword != null && searchKeyword.isBlank()) {
            searchKeyword = null;
        }

        Page<BookBorrowRequest> submittedUserBookBorrowRequest =
                bookBorrowRepository.findSubmittedUserBookBorrowRequest(
                        pageable,
                        user.getId(),
                        status,
                        searchParameter,
                        searchKeyword
                );

        List<BookBorrowResponseDto> booksResponse =
                bookMapper.toBookBorrowResponseDto(
                        submittedUserBookBorrowRequest.getContent()
                );

        return new PageResponse<>(
                booksResponse,
                submittedUserBookBorrowRequest.getNumber(),
                submittedUserBookBorrowRequest.getSize(),
                submittedUserBookBorrowRequest.getTotalElements(),
                submittedUserBookBorrowRequest.getTotalPages(),
                submittedUserBookBorrowRequest.isFirst(),
                submittedUserBookBorrowRequest.isLast()
        );
    }

    @Override
    public PageResponse<BookBorrowResponseDto> findBorrowedBooks(
            int page,
            int size,
            Authentication connectedUser,
            BookBorrowStatus status,
            String searchParameter,
            String searchKeyword
    ) {

        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("requestDate").descending()
        );

        // Handle empty strings
        if (searchParameter != null && searchParameter.isBlank()) {
            searchParameter = null;
        }

        if (searchKeyword != null && searchKeyword.isBlank()) {
            searchKeyword = null;
        }

        Page<BookBorrowRequest> submittedUserBookBorrowRequest =
                bookBorrowRepository.findBorrowedBooks(
                        pageable,
                        user.getId(),
                        status,
                        searchParameter,
                        searchKeyword
                );

        List<BookBorrowResponseDto> booksResponse =
                bookMapper.toBookBorrowResponseDto(
                        submittedUserBookBorrowRequest.getContent()
                );

        return new PageResponse<>(
                booksResponse,
                submittedUserBookBorrowRequest.getNumber(),
                submittedUserBookBorrowRequest.getSize(),
                submittedUserBookBorrowRequest.getTotalElements(),
                submittedUserBookBorrowRequest.getTotalPages(),
                submittedUserBookBorrowRequest.isFirst(),
                submittedUserBookBorrowRequest.isLast()
        );
    }

    @Override
    public PageResponse<BookBorrowResponseDto> findUserReturnedBooks(int page, int size, Authentication connectedUser, BookBorrowStatus bookBorrowStatus, String searchParameter, String searchKeyword) {
        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("requestDate").descending()
        );

        // Handle empty strings
        if (searchParameter != null && searchParameter.isBlank()) {
            searchParameter = null;
        }

        if (searchKeyword != null && searchKeyword.isBlank()) {
            searchKeyword = null;
        }

        Page<BookBorrowRequest> submittedUserBookBorrowRequest =
                bookBorrowRepository.findUserReturnedBooks(
                        pageable,
                        user.getId(),
                        bookBorrowStatus,
                        searchParameter,
                        searchKeyword
                );

        List<BookBorrowResponseDto> booksResponse =
                bookMapper.toBookBorrowResponseDto(
                        submittedUserBookBorrowRequest.getContent()
                );

        return new PageResponse<>(
                booksResponse,
                submittedUserBookBorrowRequest.getNumber(),
                submittedUserBookBorrowRequest.getSize(),
                submittedUserBookBorrowRequest.getTotalElements(),
                submittedUserBookBorrowRequest.getTotalPages(),
                submittedUserBookBorrowRequest.isFirst(),
                submittedUserBookBorrowRequest.isLast()
        );
    }

    @Override
    public BookBorrowResponseDto createBorrowRequest(BookBorrowRequestDto request, Authentication connectedUser) {

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "No book found with ID:: " + request.getBookId()
                        )
                );

        BookBorrowRequest bookBorrowRequest = bookMapper.toBookBorrowRequest(request, book, connectedUser);

        BookBorrowRequest savedBookBorrowRequest = bookBorrowRepository.save(bookBorrowRequest);

        BookBorrowResponseDto bookBorrowResponseDto = bookMapper.toBookBorrowResponse(savedBookBorrowRequest);

        return bookBorrowResponseDto;
    }


    @Override
    public Long countSharedBookByUser(boolean archived,boolean shareable,User user) {
        return bookRepository.countByArchivedAndShareableAndOwner_Id(archived,shareable,user.getId());
    }

    @Override
    public Long countBorrowedBooksByUser(User user, BookBorrowStatus status) {
        return bookBorrowRepository.countBorrowedBooksByUser(user.getId(), status);
    }

    @Override
    @Transactional
    public String updateBorrowRequestStatus(UpdateBookBorrowRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "No book found with ID:: " + request.getBookId()
                        )
                );

        BookBorrowRequest bookBorrowRequest = bookBorrowRepository.findById(request.getBorrowRequestId()).orElseThrow(() ->
                new EntityNotFoundException(
                        "No book Request found with ID:: " + request.getBorrowRequestId()
                )
        );

        book.setArchived(request.isArchived());
        book.setShareable(request.isShareable());

        bookRepository.save(book);

        bookBorrowRequest.setStatus(request.getStatus());
        bookBorrowRequest.setComment(request.getNewComment());

        bookBorrowRepository.save(bookBorrowRequest);

        if (request.getFeedbackRequest() != null) {

            Feedback feedback = feebackMapper.toFeedback(
                    request.getFeedbackRequest()
            );

            feedback.setBook(book);

            feedbackRepository.save(feedback);
        }


        return "Updated Successfully";
    }

}
