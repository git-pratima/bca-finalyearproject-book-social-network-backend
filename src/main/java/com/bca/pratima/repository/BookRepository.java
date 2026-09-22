package com.bca.pratima.repository;

import com.bca.pratima.entity.Book;
import com.bca.pratima.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    @Query("""
    SELECT book
    FROM Book book
    WHERE book.archived = false
      AND book.shareable = true
      AND book.owner.id != :userId

      AND (
            :searchKeyword IS NULL
            OR :searchKeyword = ''

            OR (
                :searchParameter = 'title'
                AND LOWER(book.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )

            OR (
                :searchParameter = 'authorName'
                AND LOWER(book.authorName) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )

            OR (
                :searchParameter = 'isbn'
                AND LOWER(book.isbn) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
      )

      AND (
            :city IS NULL
            OR :city = ''
            OR LOWER(book.address.city) LIKE LOWER(CONCAT('%', :city, '%'))
      )

      AND (
            :state IS NULL
            OR :state = ''
            OR LOWER(book.address.state) LIKE LOWER(CONCAT('%', :state, '%'))
      )

      AND (
            :postalCode IS NULL
            OR :postalCode = ''
            OR book.address.postalCode LIKE CONCAT('%', :postalCode, '%')
      )
    """)
    Page<Book> findAllDisplayableBooks(
            Pageable pageable,
            @Param("userId") Integer userId,
            @Param("searchParameter") String searchParameter,
            @Param("city") String city,
            @Param("state") String state,
            @Param("postalCode") String postalCode,
            @Param("searchKeyword") String searchKeyword
    );



    @Query("""
    SELECT b
    FROM Book b
    WHERE b.owner.id = :ownerId
      AND (
            :searchKeyword IS NULL
            OR :searchKeyword = ''
            OR (
                :searchParameter = 'title'
                AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
            OR (
                :searchParameter = 'authorName'
                AND LOWER(b.authorName) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
            OR (
                :searchParameter = 'isbn'
                AND LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
            )
      )
""")
    Page<Book> findAllBooksByOwner(
            Pageable pageable,
            @Param("ownerId") Integer ownerId,
            @Param("searchParameter") String searchParameter,
            @Param("searchKeyword") String searchKeyword
    );

    Long countByArchivedAndShareableAndOwner_Id(
            boolean archived,
            boolean shareable,
            Integer userId
    );
}
