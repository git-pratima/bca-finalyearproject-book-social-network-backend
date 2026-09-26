package com.bca.pratima.repository;

import com.bca.pratima.entity.Watchlist;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {
    List<Watchlist> findByWatchlistedAndUser_Id(Boolean watchlisted, Integer userId);

    Optional<Watchlist> findByBookIdAndUser_Id(Integer bookId, Integer userId);
}
