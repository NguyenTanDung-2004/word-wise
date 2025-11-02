package com.example.WordWise.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.WordWise.dto.request.CreateCollectionRequest;
import com.example.WordWise.dto.request.UpdateCollectionRequest;
import com.example.WordWise.dto.response.CollectionResponse;
import com.example.WordWise.entity.Collection;
import com.example.WordWise.entity.Word;
import com.example.WordWise.exception.EnumException;
import com.example.WordWise.exception.UserException;
import com.example.WordWise.repository.CollectionRepository;
import com.example.WordWise.repository.WordRepository;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private WordRepository wordRepository;

    public Collection createCollection(CreateCollectionRequest request, String userId) {
        Collection collection = Collection.builder()
                .userId(userId)
                .collectionName(request.getCollectionName())
                .description(request.getDescription())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .wordCount(0)
                .build();

        return collectionRepository.save(collection);
    }

    public List<Collection> getListCollections(int page, int pageSize, String userId) {
        int offset = page * pageSize;
        List<Collection> collections = collectionRepository.getListCollections(userId, pageSize, offset);

        // Update word count for each collection
        for (Collection collection : collections) {
            Long wordCount = wordRepository.countWordsByCollectionId(collection.getId());
            collection.setWordCount(wordCount != null ? wordCount.intValue() : 0);
        }

        return collections;
    }

    public Collection updateCollection(UpdateCollectionRequest request, String userId) {
        Optional<Collection> optionalCollection = collectionRepository.findByIdAndUserId(request.getCollectionId(), userId);

        if (optionalCollection.isEmpty()) {
            throw new UserException(EnumException.COLLECTION_NOT_FOUND);
        }

        Collection collection = optionalCollection.get();
        collection.setCollectionName(request.getCollectionName());
        collection.setDescription(request.getDescription());
        collection.setUpdatedDate(LocalDateTime.now());

        return collectionRepository.save(collection);
    }

    public void deleteCollection(String collectionId, String userId) {
        Optional<Collection> optionalCollection = collectionRepository.findByIdAndUserId(collectionId, userId);

        if (optionalCollection.isEmpty()) {
            throw new UserException(EnumException.COLLECTION_NOT_FOUND);
        }

        // Remove collectionId from all words in this collection
        List<Word> words = wordRepository.findByCollectionId(collectionId);
        for (Word word : words) {
            word.setCollectionId(null);
        }
        wordRepository.saveAll(words);

        // Delete the collection
        collectionRepository.delete(optionalCollection.get());
    }

    public List<Word> getWordsInCollection(String collectionId, String userId, int page, int pageSize) {
        Optional<Collection> optionalCollection = collectionRepository.findByIdAndUserId(collectionId, userId);

        if (optionalCollection.isEmpty()) {
            throw new UserException(EnumException.COLLECTION_NOT_FOUND);
        }

        int offset = page * pageSize;
        return wordRepository.getWordsByCollectionId(collectionId, pageSize, offset);
    }

    public Collection getCollectionById(String collectionId, String userId) {
        Optional<Collection> optionalCollection = collectionRepository.findByIdAndUserId(collectionId, userId);

        if (optionalCollection.isEmpty()) {
            throw new UserException(EnumException.COLLECTION_NOT_FOUND);
        }

        Collection collection = optionalCollection.get();
        Long wordCount = wordRepository.countWordsByCollectionId(collectionId);
        collection.setWordCount(wordCount != null ? wordCount.intValue() : 0);

        return collection;
    }

    public Long getTotalCollectionsCount(String userId) {
        return collectionRepository.countCollectionsByUserId(userId);
    }
}

