package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.collection.UpdateWordDTO;
import com.example.core_word_wise.dto.collection.WordDTO;
import com.example.core_word_wise.dto.collection.WordDetailDTO;
import com.example.core_word_wise.dto.collection.WordResponseDTO;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.entity.Word;
import com.example.core_word_wise.service.CollectionService;
import com.example.core_word_wise.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PutMapping("/{wordId}")
    public ResponseEntity<ApiResponse<WordResponseDTO>> editWord(
            @PathVariable Integer wordId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateWordDTO wordDto
    ) {
        WordResponseDTO updatedWord = wordService.editWord(wordId, user, wordDto);
        return ResponseEntity.ok(ApiResponse.success(updatedWord, "Word updated successfully."));
    }

    @GetMapping("/{wordId}")
    public ResponseEntity<ApiResponse<WordDetailDTO>> getWordDetails(
            @PathVariable Integer wordId,
            @AuthenticationPrincipal User user
    ) {
        WordDetailDTO wordDetails = wordService.getWordDetailsById(wordId, user);
        return ResponseEntity.ok(ApiResponse.success(wordDetails, "Word details retrieved successfully."));
    }
}