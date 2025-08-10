package com.example.WordWise.service;

import com.example.WordWise.dto.request.SubmitExtensionReviewRequest;
import com.example.WordWise.entity.ExtensionReview;
import com.example.WordWise.entity.Word;
import com.example.WordWise.enums.ExtensionReviewTypeEnum;
import com.example.WordWise.enums.PromptEnum;
import com.example.WordWise.exception.EnumException;
import com.example.WordWise.exception.UserException;
import com.example.WordWise.mapper.Mapper;
import com.example.WordWise.repository.ReviewExtensionRepository;
import com.example.WordWise.utils.APIUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Value("${external.gemini-api-json}")
    private String geminiAPIJsonPath;

    @Value("${external.gemini-url}")
    private String geminiURL;

    @Autowired
    private APIUtils apiUtils;

    private List<Integer> options = Arrays.asList(2);

    @Transactional(rollbackFor = Exception.class)
    public ExtensionReview getExtensionReview(String userId) {
        int randomIndex = ThreadLocalRandom.current().nextInt(options.size());
        ExtensionReviewTypeEnum type = ExtensionReviewTypeEnum.fromId(options.get(randomIndex));

        Word word = wordService.getWordForReviewExtension(userId, type);

        ExtensionReview extensionReview = new ExtensionReview();
        if (type.equals(ExtensionReviewTypeEnum.SELECT) && Objects.isNull(word.getDescription())) {
            try {
                genDescriptionForSELECT(word, extensionReview);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            wordService.setExtensionReviewValue(extensionReview.getDescription(), extensionReview.getOptions(), word);
        }

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

    private void genDescriptionForSELECT(Word word, ExtensionReview extensionReview) throws JsonProcessingException {
        String prompt = PromptEnum.GEN_DESCRIPTION.getValue();
        prompt = prompt.replaceAll("<replaced>", word.getEnglishWord());
        prompt = this.apiUtils.escape(prompt);

        String jsonContent = "";
        try {
            jsonContent = this.apiUtils.loadJsonConfig(geminiAPIJsonPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jsonContent = this.apiUtils.replaceValue(Arrays.asList("${replaced}"), Arrays.asList(prompt), jsonContent);

        ResponseEntity<String> response = this.apiUtils.callApi(
                new HashMap<>(), jsonContent, geminiURL, HttpMethod.POST
        );

        String bodyResponse = response.getBody();
        if (bodyResponse == null) {
            throw new RuntimeException("API response body is null");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(bodyResponse);

        // Get the "text" content
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        // Split into lines and parse
        String description = "", option1 = "", option2 = "", option3 = "";

        for (String line : text.split("\\n")) {
            if (line.startsWith("description:")) {
                description = line.replaceFirst("description:\\s*", "");
            } else if (line.startsWith("option1:")) {
                option1 = line.replaceFirst("option1:\\s*", "");
            } else if (line.startsWith("option2:")) {
                option2 = line.replaceFirst("option2:\\s*", "");
            } else if (line.startsWith("option3:")) {
                option3 = line.replaceFirst("option3:\\s*", "");
            }
        }

        extensionReview.setDescription(description);
        extensionReview.setOptions(Arrays.asList(option1, option2, option3));
    }
}
