package com.bca.pratima.mapper;

import com.bca.pratima.dto.FeedbackRequest;
import com.bca.pratima.dto.FeedbackResponse;
import com.bca.pratima.entity.Book;
import com.bca.pratima.entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        if(request!=null){
            feedback.setRating(request.getRating());
            feedback.setComment(request.getComment());
            return feedback;
        }
        return null;
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return null;
    }
}
