package com.bca.pratima.repository;

import com.bca.pratima.dto.FeedbackRequest;
import com.bca.pratima.dto.FeedbackResponse;
import com.bca.pratima.dto.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public interface FeedbackService {

    public Integer save(FeedbackRequest request, Authentication connectedUser);

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser);
}
