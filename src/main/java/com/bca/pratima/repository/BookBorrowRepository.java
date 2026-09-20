package com.bca.pratima.repository;

import com.bca.pratima.entity.BookBorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookBorrowRepository extends JpaRepository<BookBorrowRequest, Integer> {
}
