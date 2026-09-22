package com.bca.pratima.controller;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.dto.*;
import com.bca.pratima.entity.BookBorrowRequest;
import com.bca.pratima.repository.BookBorrowRepository;
import com.bca.pratima.service.BookService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.save(request, connectedUser));
    }

    @GetMapping("/borrow-request")
    public ResponseEntity<Response> findSubmittedUserBookBorrowRequest(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "5", required = false) int size,
            @RequestParam(name = "status", required = false) BookBorrowStatus bookBorrowStatus,
            @RequestParam(name = "searchParameter", required = false) String searchParameter,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword,
            Authentication connectedUser
    ) {

        PageResponse<BookBorrowResponseDto> userBookBorrowRequest =  bookService.findSubmittedUserBookBorrowRequest(page, size, connectedUser, bookBorrowStatus,searchParameter, searchKeyword);
        Status status = new Status();
        status.setStatus(HttpStatus.OK.value());
        status.setMessage("Submitted User Book Borrow Request.");

        Response response = new Response();
        response.setStatus(status);
        response.setData(userBookBorrowRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/borrowed")
    public ResponseEntity<Response> findBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "5", required = false) int size,
            @RequestParam(name = "status", required = false) BookBorrowStatus bookBorrowStatus,
            @RequestParam(name = "searchParameter", required = false) String searchParameter,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword,
            Authentication connectedUser
    ) {

        PageResponse<BookBorrowResponseDto> userBookBorrowRequest =  bookService.findBorrowedBooks(page, size, connectedUser, bookBorrowStatus,searchParameter, searchKeyword);
        Status status = new Status();
        status.setStatus(HttpStatus.OK.value());
        status.setMessage("Submitted User Book Borrow Request.");

        Response response = new Response();
        response.setStatus(status);
        response.setData(userBookBorrowRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer bookId
    ) {
        return ResponseEntity.ok(bookService.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "searchParameter", required = false) String searchParameter,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "state", required = false) String postalCode,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser, searchParameter,city,state,postalCode,searchKeyword));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "searchParameter", required = false) String searchParameter,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser,searchParameter,searchKeyword));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        //TODO
        return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
    }

    @PostMapping("/borrow-book")
    public ResponseEntity<Response> createBorrowRequest(
            @Valid @RequestBody BookBorrowRequestDto request,
            Authentication connectedUser
    ) {
        BookBorrowResponseDto book = bookService.createBorrowRequest(request,connectedUser);
        Status status = new Status();
        status.setStatus(HttpStatus.OK.value());
        status.setMessage("Book Borrow Request Submitted.");

        Response response = new Response();
        response.setStatus(status);
        response.setData(book);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        //TODO
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, connectedUser));
    }

    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        //TODO
        return ResponseEntity.ok(bookService.approveReturnBorrowedBook(bookId, connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        bookService.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build();
    }
}
