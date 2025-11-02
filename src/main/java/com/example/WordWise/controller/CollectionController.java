package com.example.WordWise.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.WordWise.dto.request.CreateCollectionRequest;
import com.example.WordWise.dto.request.UpdateCollectionRequest;
import com.example.WordWise.dto.response.ApiResponse;
import com.example.WordWise.dto.response.EnumResponse;
import com.example.WordWise.entity.Collection;
import com.example.WordWise.entity.Word;
import com.example.WordWise.service.CollectionService;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping("/create")
    public ResponseEntity createCollection(
            @RequestBody CreateCollectionRequest request,
            Authentication authentication) {

        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        Collection collection = collectionService.createCollection(request, userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(collection)
                .enumResponse(EnumResponse.toJson(EnumResponse.CREATE_COLLECTION_SUCCESS))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user-collections")
    public ResponseEntity getUserCollections(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        List<Collection> collections = collectionService.getListCollections(page, size, userId);
        Long totalCount = collectionService.getTotalCollectionsCount(userId);

        Map<String, Object> result = Map.of(
                "collections", collections,
                "totalCount", totalCount,
                "currentPage", page,
                "pageSize", size
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .object(result)
                .enumResponse(EnumResponse.toJson(EnumResponse.GET_COLLECTIONS_SUCCESS))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update")
    public ResponseEntity updateCollection(
            @RequestBody UpdateCollectionRequest request,
            Authentication authentication) {

        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        Collection collection = collectionService.updateCollection(request, userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(collection)
                .enumResponse(EnumResponse.toJson(EnumResponse.UPDATE_COLLECTION_SUCCESS))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete/{collectionId}")
    public ResponseEntity deleteCollection(
            @PathVariable String collectionId,
            Authentication authentication) {

        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        collectionService.deleteCollection(collectionId, userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(null)
                .enumResponse(EnumResponse.toJson(EnumResponse.DELETE_COLLECTION_SUCCESS))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{collectionId}/words")
    public ResponseEntity getWordsInCollection(
            @PathVariable String collectionId,
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        List<Word> words = collectionService.getWordsInCollection(collectionId, userId, page, size);
        Collection collection = collectionService.getCollectionById(collectionId, userId);

        Map<String, Object> result = Map.of(
                "collection", collection,
                "words", words,
                "currentPage", page,
                "pageSize", size
        );

        ApiResponse apiResponse = ApiResponse.builder()
                .object(result)
                .enumResponse(EnumResponse.toJson(EnumResponse.GET_COLLECTION_WORDS_SUCCESS))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity getCollectionById(
            @PathVariable String collectionId,
            Authentication authentication) {

        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        Collection collection = collectionService.getCollectionById(collectionId, userId);

        ApiResponse apiResponse = ApiResponse.builder()
                .object(collection)
                .enumResponse(EnumResponse.toJson(EnumResponse.GET_COLLECTIONS_SUCCESS))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

