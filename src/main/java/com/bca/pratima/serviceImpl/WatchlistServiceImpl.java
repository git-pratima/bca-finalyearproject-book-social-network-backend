package com.bca.pratima.serviceImpl;

import com.bca.pratima.dto.BookResponse;
import com.bca.pratima.entity.Book;
import com.bca.pratima.entity.User;
import com.bca.pratima.entity.Watchlist;
import com.bca.pratima.mapper.WatchlistMapper;
import com.bca.pratima.repository.BookBorrowRepository;
import com.bca.pratima.repository.BookRepository;
import com.bca.pratima.repository.WatchlistRepository;
import com.bca.pratima.service.WatchlistService;
import com.bca.pratima.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WatchlistMapper watchlistMapper;

    @Override
    public Watchlist addToWatchlist(Integer bookId, Authentication connectedUser) {

        Watchlist res = null;

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        User user = userUtils.getLoggedInUser(connectedUser);

        Optional<Watchlist> watchlistOptional = watchlistRepository.findByBookIdAndUser_Id(bookId,user.getId());

        if (watchlistOptional.isPresent()) {
            Watchlist watchlist = watchlistOptional.get();
            boolean currentlyWatchlisted = Boolean.TRUE.equals(watchlist.getWatchlisted());
            watchlist.setWatchlisted(!currentlyWatchlisted);
            res = watchlistRepository.save(watchlist);
        } else {
            Watchlist watchlist = new Watchlist();
            watchlist.setBookId(bookId); // if you have a Book relationship
            watchlist.setUser(user);
            watchlist.setWatchlisted(true);
            res = watchlistRepository.save(watchlist);
        }
        return res;
    }

    @Override
    public List<BookResponse> getWatchlistedBook(Authentication connectedUser) {
        User user = userUtils.getLoggedInUser(connectedUser);
        List<Watchlist> watchList = watchlistRepository.findByWatchlistedAndUser_Id(true,user.getId());
        if(watchList!=null && watchList.size()>0){
            return watchlistMapper.toBookResponse(watchList);
        }
        return List.of();
    }

}
