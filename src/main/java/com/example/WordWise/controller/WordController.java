package com.example.WordWise.controller;

import java.util.List;
import java.util.Map;

import com.example.WordWise.entity.User;
import com.example.WordWise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.WordWise.dto.request.EditWordRequest;
import com.example.WordWise.dto.request.InsertWordRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.Word;
import com.example.WordWise.service.WordService;


@RestController
@RequestMapping("/word")
public class WordController {
    @Autowired
    private WordService wordService;

    @Autowired
    private UserService userService;

    @PostMapping("/insertWord")
    public ResponseEntity insertWord(@RequestBody InsertWordRequest insertWordRequest) {
        Word word = this.wordService.insertWord(insertWordRequest);

        ApiResponse apiResponse = ApiResponse.builder()
        .object(word)
        .enumResponse(EnumResponse.toJson(EnumResponse.INSERT_WORD_SUCCESS))    
        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/editWord")
    public ResponseEntity editWord(@RequestBody EditWordRequest editWordRequest) {
        Word word = this.wordService.editWord(editWordRequest);
        ApiResponse apiResponse = ApiResponse.builder()
        .object(word)
        .enumResponse(EnumResponse.toJson(EnumResponse.EDIT_WORD_SUCCESS))    
        .build();

        return ResponseEntity.ok(apiResponse);
    }

    //@PostAuthorize("hasRole('USER')")
    @GetMapping("/words")
    public ResponseEntity getListWord(Authentication authentication, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");
        User user = this.userService.getUserFromId(userId);
        List<Word> list = this.wordService.getListWords(page, size, user.getUserId());
        ApiResponse apiResponse = ApiResponse.builder()
        .object(list)
        .enumResponse(EnumResponse.toJson(EnumResponse.EDIT_WORD_SUCCESS))
        .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/gen-idiom/{wordId}")
    public ResponseEntity generateIdiom(@PathVariable(name = "wordId") String wordId) {
        Map<String, String> idiomMap = this.wordService.generateIdiom(wordId);
        ApiResponse apiResponse = ApiResponse.builder()
        .object(idiomMap)
        .enumResponse(EnumResponse.toJson(EnumResponse.DONE))
        .build();
        return ResponseEntity.ok(apiResponse);
    }
}
