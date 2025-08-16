package com.example.WordWise.service;

import java.util.*;

import com.example.WordWise.enums.ExtensionReviewTypeEnum;
import com.example.WordWise.enums.PromptEnum;
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

import com.example.WordWise.dto.request.EditWordRequest;
import com.example.WordWise.dto.request.InsertWordRequest;
import com.example.WordWise.entity.Word;
import com.example.WordWise.exception.EnumException;
import com.example.WordWise.exception.UserException;
import com.example.WordWise.mapper.Mapper;
import com.example.WordWise.repository.WordRepository;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    @Qualifier("wordMapper")
    private Mapper mapper;

    @Autowired
    private APIUtils apiUtils;

    @Value("${external.gemini-api-json}")
    private String geminiAPIJsonPath;

    @Value("${external.gemini-url}")
    private String geminiURL;

    public String translatedWord(String word) {
        return null;
    }

    public Word insertWord(InsertWordRequest insertWordRequest) {
        Word word = new Word();
        word = (Word) this.mapper.map(insertWordRequest, word);

        word = this.wordRepository.save(word);
        return word;
    }

    public Word editWord(EditWordRequest editWordRequest) {
        Word word = findWordById(editWordRequest.getWordId());

        if (Objects.isNull(word)) {
            throw new UserException(EnumException.WORD_NOT_FOUND);
        }

        word = (Word) this.mapper.map(editWordRequest, word);
        word = this.wordRepository.save(word);
        return word;
    }

    private Word findWordById(String id) {
        Optional optWord = this.wordRepository.findById(id);

        if (optWord.isPresent()) {
            return (Word) optWord.get();
        }
        return null;
    }

    public List<Word> getListWords(int page, int pageSize, String userId) {
        List<Word> list = this.wordRepository.getListWords(userId, pageSize, page);
        return list;
    }

    public Word getWordForReviewExtension(String userId, ExtensionReviewTypeEnum typeEnum) {
        return this.wordRepository.getReviewExtensionWord(userId, typeEnum.getId());
    }

    public void setExtensionReviewValue(String description, List<String> options, Word word) {
        word.setDescription(description);
        word.setOptions(options);
        this.wordRepository.save(word);
    }

    public Map<String, String> generateIdiom(String wordId) {
        Word word = findWordById(wordId);
        if (Objects.isNull(word)) {
            throw new UserException(EnumException.WORD_NOT_FOUND);
        }

        String enumPromptValue = PromptEnum.GEN_IDIOM.getValue();
        enumPromptValue = enumPromptValue.replaceAll("<word>", word.getEnglishWord());

        String escapedPrompt = apiUtils.escape(enumPromptValue);

        String jsonContent = "";
        try {
            jsonContent = this.apiUtils.loadJsonConfig(geminiAPIJsonPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        jsonContent = this.apiUtils.replaceValue(Arrays.asList("${replaced}"), Arrays.asList(escapedPrompt), jsonContent);

        ResponseEntity<String> response = this.apiUtils.callApi(
                new HashMap<>(), jsonContent, geminiURL, HttpMethod.POST
        );

        String bodyResponse = response.getBody();
        if (bodyResponse == null) {
            throw new RuntimeException("API response body is null");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;

        try {
            root = mapper.readTree(bodyResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

// Extract "text" content
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

// Remove triple backticks and "json" label if present
        text = text.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

// Parse the extracted JSON
        JsonNode idiomNode = null;
        try {
            idiomNode = mapper.readTree(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse idiom JSON: " + text, e);
        }

// Get values
        Map<String, String> idiomMap = new HashMap<>();

        String idiom = idiomNode.path("idiom").asText();
        idiomMap.put("idiom", idiom);
        if (idiom.equals("")) {
            return idiomMap;
        }

        String vietnamese = idiomNode.path("vietnamese").asText();
        idiomMap.put("vietnamese", vietnamese);

        return idiomMap;
    }
}
