package com.bca.pratima.repository;

import com.bca.pratima.entity.Watchlist;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {
    List<Watchlist> findByWatchlistedAndUser_Id(Boolean watchlisted, Integer userId);

    Optional<Watchlist> findByBookIdAndUser_Id(Integer bookId, Integer userId);

    @Query("""
    SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END
    FROM Watchlist w
    WHERE w.bookId = :bookId
      AND w.user.id = :userId
      AND w.watchlisted = true
""")
    Boolean checkIfBookIsWatchlisted(
            @Param("bookId") Integer bookId,
            @Param("userId") Integer userId
    );
}
