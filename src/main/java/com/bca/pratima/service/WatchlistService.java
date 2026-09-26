package com.bca.pratima.service;

import com.bca.pratima.dto.BookResponse;
import com.bca.pratima.entity.Watchlist;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WatchlistService {
    Watchlist addToWatchlist(Integer bookId, Authentication connectedUser);

    List<BookResponse> getWatchlistedBook(Authentication connectedUser);
}
