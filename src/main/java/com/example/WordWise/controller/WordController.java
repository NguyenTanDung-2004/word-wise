package com.example.WordWise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.WordWise.dto.request.EditWordRequest;
import com.example.WordWise.dto.request.InsertWordRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.Word;
import com.example.WordWise.service.WordService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/word")
public class WordController {
    @Autowired
    private WordService wordService;

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

    @GetMapping("/words")
    public ResponseEntity getListWord(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        
    }

}
