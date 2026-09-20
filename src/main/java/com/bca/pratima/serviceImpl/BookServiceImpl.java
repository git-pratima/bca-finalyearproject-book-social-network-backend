package com.bca.pratima.serviceImpl;

import com.bca.pratima.dto.*;
import com.bca.pratima.entity.*;
import com.bca.pratima.exception.AccessDeniedException;
import com.bca.pratima.exception.OperationNotPermittedException;
import com.bca.pratima.mapper.AddressMapper;
import com.bca.pratima.mapper.BookMapper;
import com.bca.pratima.repository.BookBorrowRepository;
import com.bca.pratima.repository.BookRepository;
import com.bca.pratima.repository.BookTransactionHistoryRepository;
import com.bca.pratima.service.BookService;
import com.bca.pratima.service.FileStorageService;
import com.bca.pratima.utils.CloudinaryImageUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.bca.pratima.repository.BookSpecification.withOwnerId;

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
    private BookTransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CloudinaryImageUtils cloudinaryImageUtils;

    @Autowired
    private BookBorrowRepository bookBorrowRepository;

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
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
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
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
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
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }

        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("Te requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }
    @Override
    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
    @Override
    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
//    @Override
//    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
//        User user = ((User) connectedUser.getPrincipal());
//        var profilePicture = fileStorageService.saveFile(file, bookId, user.getId());
//        book.setBookCover(profilePicture);
//        bookRepository.save(book);
//    }

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
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }
    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
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
}
