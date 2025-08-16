package com.example.WordWise.mapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.WordWise.dto.request.EditWordRequest;
import com.example.WordWise.dto.request.InsertWordRequest;
import com.example.WordWise.entity.Word;
@Component("wordMapper")
public class WordMapper implements Mapper{

    @Override
    public Object map(Object source, Object target) {
        if (source instanceof InsertWordRequest && target instanceof Word) {
            return convertRequestInsertToEntity((InsertWordRequest) source, (Word) target);
        }
        if (source instanceof EditWordRequest && target instanceof Word) {
            return convertRequestEditToEntity((EditWordRequest) source, (Word) target);
        }
        return null;
    }

    public Word convertRequestInsertToEntity(InsertWordRequest insertWordRequest, Word word) {
        word.setContext(insertWordRequest.getContext());
        word.setEnglishWord(insertWordRequest.getEnglishWord());
        word.setVietnameseWord(insertWordRequest.getVietnameseWord());
        word.setIsExtension(insertWordRequest.getIsExtension());
        word.setNote(insertWordRequest.getNote());
        word.setUserId(insertWordRequest.getUserId());
        word.setCreatedDate(LocalDateTime.now());
        word.setUpdatedDate(LocalDateTime.now());
        return word;
    }
    
    public Word convertRequestEditToEntity(EditWordRequest editWordRequest, Word word) {
        word.setContext(editWordRequest.getContext());
        word.setEnglishWord(editWordRequest.getEnglishWord());
        word.setVietnameseWord(editWordRequest.getVietnameseWord());
        word.setIsExtension(editWordRequest.getIsExtension());
        word.setNote(editWordRequest.getNote());
        word.setCreatedDate(LocalDateTime.now());
        word.setUpdatedDate(LocalDateTime.now());

        // additional fields for adding word manually
        word.setIdiom(editWordRequest.getIdiom());
        word.setExample(editWordRequest.getExample());
        word.setPhonetic(editWordRequest.getPhonetic());
        word.setPartOfSpeech(editWordRequest.getPartOfSpeech());
        word.setAudioUrl(editWordRequest.getAudioUrl());
        return word;
    }
}
