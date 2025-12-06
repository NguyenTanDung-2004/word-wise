package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.collection.UpdateWordDTO;
import com.example.core_word_wise.dto.collection.WordDTO;
import com.example.core_word_wise.dto.collection.WordDetailDTO;
import com.example.core_word_wise.dto.collection.WordResponseDTO;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.entity.Word;
import com.example.core_word_wise.repository.UserWordRepository;
import com.example.core_word_wise.repository.WordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final UserWordRepository userWordRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public WordDetailDTO getWordDetailsById(Integer wordId, User user) {
        if (!userWordRepository.existsByUserAndWord_WordId(user, wordId)) {
            throw new SecurityException("User does not have permission to access this word.");
        }

        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("Word not found with id: " + wordId));

        return  mapToWordDetailDTO(word);
    }


    @Transactional
    public WordResponseDTO editWord(Integer wordId, User user, UpdateWordDTO wordDto) {
        // Kiểm tra xem người dùng có sở hữu từ này không (tức là từ có nằm trong bất kỳ collection nào của họ không)
        if (!userWordRepository.existsByUserAndWord_WordId(user, wordId)) {
            throw new SecurityException("User does not have permission to edit this word.");
        }

        Word wordToUpdate = wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("Word not found with id: ".concat(wordId.toString())));

        boolean isUpdated = false;

        // Cập nhật wordText (nếu có)
        if (wordDto.getWord() != null && !wordDto.getWord().isBlank()) {
            wordToUpdate.setWordText(wordDto.getWord());
            isUpdated = true;
        }

        // Cập nhật wordVn (nếu có)
        if (wordDto.getWord_vn() != null) {
            wordToUpdate.setWordVn(wordDto.getWord_vn());
            isUpdated = true;
        }

        // Cập nhật partOfSpeech (nếu có)
        if (wordDto.getPartOfSpeech() != null) {
            wordToUpdate.setPartOfSpeech(wordDto.getPartOfSpeech());
            isUpdated = true;
        }

        // Cập nhật các trường định nghĩa và nguồn (nếu có)
        if (wordDto.getDefinition_en() != null) {
            wordToUpdate.setDefinitionEn(wordDto.getDefinition_en());
            isUpdated = true;
        }
        if (wordDto.getDefinition_vi() != null) {
            wordToUpdate.setDefinitionVi(wordDto.getDefinition_vi());
            isUpdated = true;
        }
        if (wordDto.getSource() != null) {
            wordToUpdate.setSourceUrl(wordDto.getSource());
            isUpdated = true;
        }

        // Cập nhật các trường JSON (nếu có)
        try {
            if (wordDto.getPhonetics() != null) {
                wordToUpdate.setPhonetics(objectMapper.writeValueAsString(wordDto.getPhonetics()));
                isUpdated = true;
            }
            if (wordDto.getExamples() != null) {
                wordToUpdate.setExamples(objectMapper.writeValueAsString(wordDto.getExamples()));
                isUpdated = true;
            }
            if (wordDto.getIdioms_collocations() != null) {
                wordToUpdate.setIdiomsCollocations(objectMapper.writeValueAsString(wordDto.getIdioms_collocations()));
                isUpdated = true;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize word details to JSON", e);
        }

        // Cập nhật synonyms (nếu có)
        if (wordDto.getSynonyms() != null) {
            wordToUpdate.setSynonyms(String.join(",", wordDto.getSynonyms()));
            isUpdated = true;
        }

        // Chỉ lưu lại nếu có sự thay đổi
        Word savedWord = null;
        if (isUpdated) {
            savedWord = wordRepository.save(wordToUpdate);
        }

        return mapToWordResponseDTO(savedWord);
    }


    private WordResponseDTO mapToWordResponseDTO(Word word) {
        return WordResponseDTO.builder()
                .wordId(word.getWordId())
                .wordText(word.getWordText())
                .wordVn(word.getWordVn())
                .partOfSpeech(word.getPartOfSpeech())
                .definitionEn(word.getDefinitionEn())
                .definitionVi(word.getDefinitionVi())
                .sourceUrl(word.getSourceUrl())
                .phonetics(toJsonNode(word.getPhonetics()))
                .examples(toJsonNode(word.getExamples()))
                .idiomsCollocations(toJsonNode(word.getIdiomsCollocations()))
                .phrasalVerbs(toJsonNode(word.getPhrasalVerbs()))
                .synonyms(word.getSynonyms())
                .build();
    }

    private WordDetailDTO mapToWordDetailDTO(Word word) {
        return WordDetailDTO.builder()
                .wordId(word.getWordId())
                .wordText(word.getWordText())
                .wordVn(word.getWordVn())
                .partOfSpeech(word.getPartOfSpeech())
                .definitionEn(word.getDefinitionEn())
                .definitionVi(word.getDefinitionVi())
                .sourceUrl(word.getSourceUrl())
                .phonetics(toJsonNode(word.getPhonetics()))
                .examples(toJsonNode(word.getExamples()))
                .idiomsCollocations(toJsonNode(word.getIdiomsCollocations()))
                .phrasalVerbs(toJsonNode(word.getPhrasalVerbs()))
                .synonyms(word.getSynonyms())
                .build();
    }

    private JsonNode toJsonNode(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            return null;
        }
    }
}