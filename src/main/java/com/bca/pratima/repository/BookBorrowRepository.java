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
        WHERE b.borrower.id = :id
        AND b.status = :status""")
    Page<BookBorrowRequest> findSubmittedUserBookBorrowRequest(Pageable pageable,
                                                               @Param("id") Integer id,
                                                               @Param("status") BookBorrowStatus status);
}
