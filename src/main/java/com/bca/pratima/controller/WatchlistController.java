package com.bca.pratima.controller;

import com.bca.pratima.dto.BookResponse;
import com.bca.pratima.dto.Response;
import com.bca.pratima.dto.Status;
import com.bca.pratima.dto.UserProfile;
import com.bca.pratima.entity.Watchlist;
import com.bca.pratima.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("watchlist")
@RequiredArgsConstructor
@Tag(name = "Watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/add-watchlist")
    @Operation(summary = "Add a book to watchlist")
    public ResponseEntity<Response> addToWatchlist(
            @RequestParam Integer bookId,
            Authentication connectedUser
    ) {
        Watchlist watchlist = watchlistService.addToWatchlist(bookId, connectedUser);
        Status status = new Status();
        status.setStatus(HttpStatus.OK.value());
        status.setMessage("Book has been added in Your Watchlist.");

        Response response = new Response();
        response.setStatus(status);
        response.setData(watchlist);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get-watchlist")
    public ResponseEntity<Response> getWatchlistedBook( Authentication connectedUser) {
        List<BookResponse> data = watchlistService.getWatchlistedBook(connectedUser);
        Status status = new Status();
        status.setStatus(HttpStatus.OK.value());
        status.setMessage("User Watchlist retrieved.");

        Response response = new Response();
        response.setStatus(status);
        response.setData(data);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
