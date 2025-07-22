package com.example.WordWise.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
}
