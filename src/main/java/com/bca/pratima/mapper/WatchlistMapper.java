package com.bca.pratima.mapper;

import com.bca.pratima.dto.BookResponse;
import com.bca.pratima.entity.Book;
import com.bca.pratima.entity.Watchlist;
import com.bca.pratima.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WatchlistMapper {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookRepository bookRepository;

    public List<BookResponse> toBookResponse(List<Watchlist> watchlists){
        List<BookResponse> response = new ArrayList<BookResponse>();
        for(Watchlist watchlist : watchlists){
            Book book = bookRepository.findById(watchlist.getBookId()).get();
            BookResponse res = bookMapper.toBookResponseWithoutAddress(book);
            response.add(res);
        }
        return response;
    }

}
