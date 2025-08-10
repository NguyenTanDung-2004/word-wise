package com.example.WordWise.service;

import com.example.WordWise.dto.request.SubmitExtensionReviewRequest;
import com.example.WordWise.entity.ExtensionReview;
import com.example.WordWise.entity.Word;
import com.example.WordWise.enums.ExtensionReviewTypeEnum;
import com.example.WordWise.exception.EnumException;
import com.example.WordWise.exception.UserException;
import com.example.WordWise.mapper.Mapper;
import com.example.WordWise.repository.ReviewExtensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExtensionReviewService {
    @Autowired
    private WordService wordService;

    @Autowired
    private ReviewExtensionRepository reviewExtensionRepository;

    @Autowired
    @Qualifier("reviewExtensionMapper")
    private Mapper mapper;

    private List<Integer> options = Arrays.asList(1, 3);

    public ExtensionReview getExtensionReview(String userId) {
        int randomIndex = ThreadLocalRandom.current().nextInt(options.size());
        ExtensionReviewTypeEnum type = ExtensionReviewTypeEnum.fromId(options.get(randomIndex));

        Word word = wordService.getWordForReviewExtension(userId, type);

        ExtensionReview extensionReview = new ExtensionReview();
        extensionReview = (ExtensionReview) mapper.map(type, word);

        extensionReview = reviewExtensionRepository.save(extensionReview);
        return extensionReview;
    }

    public void submit(SubmitExtensionReviewRequest request) {
        ExtensionReview review = getReviewById(request.getReviewId());

        review.setIsTrue(request.getIsTrue());
        reviewExtensionRepository.save(review);
    }

    private ExtensionReview getReviewById(String id) {
        Optional<ExtensionReview> opt = this.reviewExtensionRepository.findById(id);
        if (!opt.isPresent()) {
            throw new UserException(EnumException.EXTENSION_REVIEW_NOT_FOUND);
        }

        return opt.get();
    }
}
