package com.bca.pratima.repository;

import com.bca.pratima.appenum.BookBorrowStatus;
import com.bca.pratima.entity.BookBorrowRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookBorrowRepository extends JpaRepository<BookBorrowRequest, Integer> {

    @Query("""
    SELECT b
    FROM BookBorrowRequest b
    WHERE b.bookOwner.id = :id
      AND b.borrower.id <> :id
      AND (:status IS NULL OR b.status = :status)
      AND (
            :searchKeyword IS NULL
            OR :searchKeyword = ''
            OR (
                :searchParameter = 'title'
                AND LOWER(b.book.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
            OR (
                :searchParameter = 'authorName'
                AND LOWER(b.book.authorName) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
            OR (
                :searchParameter = 'isbn'
                AND LOWER(b.book.isbn) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
      )
    """)
    Page<BookBorrowRequest> findSubmittedUserBookBorrowRequest(
            Pageable pageable,
            @Param("id") Integer id,
            @Param("status") BookBorrowStatus status,
            @Param("searchParameter") String searchParameter,
            @Param("searchKeyword") String searchKeyword
    );


    @Query("""
    SELECT b
    FROM BookBorrowRequest b
    WHERE b.borrower.id = :id
    AND (:status IS NULL OR b.status = :status)
    AND (
        :searchKeyword IS NULL
        OR :searchKeyword = ''
        OR (
            (:searchParameter = 'title'
                AND LOWER(b.book.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))
            OR
            (:searchParameter = 'authorName'
                AND LOWER(b.book.authorName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))
            OR
            (:searchParameter = 'isbn'
                AND LOWER(b.book.isbn) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))
        )
    )
""")
    Page<BookBorrowRequest> findBorrowedBooks(
            Pageable pageable,
            @Param("id") Integer id,
            @Param("status") BookBorrowStatus status,
            @Param("searchParameter") String searchParameter,
            @Param("searchKeyword") String searchKeyword
    );


    @Query("""
    SELECT COUNT(b)
    FROM BookBorrowRequest b
    WHERE b.borrower.id = :userId
    AND b.status = :status""")
    Long countBorrowedBooksByUser(@Param("userId") Integer userId, @Param("status") BookBorrowStatus status);

    @Query("""
    SELECT b
    FROM BookBorrowRequest b
    WHERE b.borrower.id = :id
    AND (:status IS NULL OR b.status = :status)
    AND (:searchKeyword IS NULL OR :searchKeyword = '' OR (
            (:searchParameter = 'title'
                AND LOWER(b.book.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))
            OR (:searchParameter = 'authorName'
                AND LOWER(b.book.authorName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))
            OR (:searchParameter = 'isbn' AND LOWER(b.book.isbn) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))))""")
    Page<BookBorrowRequest> findUserReturnedBooks(Pageable pageable, Integer id, BookBorrowStatus status, String searchParameter, String searchKeyword);
}
