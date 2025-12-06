package com.example.core_word_wise.controller;

import com.example.core_word_wise.dto.ApiResponse;
import com.example.core_word_wise.dto.collection.*;
import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.entity.User;
import com.example.core_word_wise.entity.UserWord;
import com.example.core_word_wise.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;


    // lấy danh sách tên của collection
    @GetMapping("/names")
    public ResponseEntity<ApiResponse<List<String>>> getCollectionNames(@AuthenticationPrincipal User user) {
        List<String> names = collectionService.getCollectionNames(user);
        return ResponseEntity.ok(ApiResponse.success(names, "Collection names retrieved successfully."));
    }

    @GetMapping("/{collectionName}")
    public ResponseEntity<ApiResponse<CollectionDetailDTO>> getCollectionDetails(
            @PathVariable String collectionName,
            @AuthenticationPrincipal User user
    ) {
        CollectionDetailDTO collectionDetails = collectionService.getCollectionDetailsByName(collectionName, user);
        return ResponseEntity.ok(ApiResponse.success(collectionDetails, "Collection details retrieved successfully."));
    }

    // lấy danh sách collection của user
    @GetMapping
    public ResponseEntity<ApiResponse<List<CollectionSummaryDTO>>> getAllUserCollections(@AuthenticationPrincipal User user) {
        List<CollectionSummaryDTO> collections = collectionService.getAllUserCollections(user);
        return ResponseEntity.ok(ApiResponse.success(collections, "Collections retrieved successfully."));
    }

    // tạo collection mới
    @PostMapping
    public ResponseEntity<ApiResponse<CollectionResponseDTO>> createCollection(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CollectionRequest request
    ) {
        CollectionResponseDTO newCollection = collectionService.createCollection(user, request);
        return new ResponseEntity<>(ApiResponse.success(newCollection, "Collection created successfully."), HttpStatus.CREATED);
    }


    // chỉnh sửa collection theo id (name, description)
    @PutMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<CollectionResponseDTO>> updateCollection(
            @PathVariable Integer collectionId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CollectionRequest request
    ) {
        CollectionResponseDTO updatedCollection = collectionService.updateCollection(collectionId, user, request);
        return ResponseEntity.ok(ApiResponse.success(updatedCollection, "Collection updated successfully."));
    }

    // xóa collection (cập nhật isDeleted)
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(
            @PathVariable Integer collectionId,
            @AuthenticationPrincipal User user
    ) {
        collectionService.softDeleteCollection(collectionId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Collection deleted successfully."));
    }


    // thêm từ vào collection
    @PostMapping("/add-words")
    public ResponseEntity<ApiResponse<AddWordResponseDTO>> addWordsToCollection(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddWordRequest request
    ) {
        if (user == null && request.getUserId() == null) {
            return new ResponseEntity<>(ApiResponse.error("Authentication required. Provide a token or userId."), HttpStatus.UNAUTHORIZED);
        }

        AddWordResponseDTO result = collectionService.addWordsToCollection(user, request);

        String message = String.format("Processed %d words. %d added successfully, %d failed.",
                request.getWords().size(), result.getSuccess().size(), result.getFailed().size());

        return new ResponseEntity<>(ApiResponse.success(result, message), HttpStatus.OK);
    }

    // tạo collection cùng với list từ vựng
    @PostMapping("/add-word/bulk")
    public ResponseEntity<ApiResponse<CollectionResponseDTO>> createCollectionWithWords(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCollectionWithWordsRequest request
    ) {
        CollectionResponseDTO newCollection = collectionService.createCollectionWithWords(user, request);
        return new ResponseEntity<>(ApiResponse.success(newCollection, "Collection and words created successfully."), HttpStatus.CREATED);
    }

    // xóa một từ ra khỏi collection
    @DeleteMapping("/{collectionId}/words/{wordId}")
    public ResponseEntity<ApiResponse<Void>> removeWordFromCollection(
            @PathVariable Integer collectionId,
            @PathVariable Integer wordId,
            @AuthenticationPrincipal User user
    ) {
        collectionService.removeWordFromCollection(collectionId, wordId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Word removed from collection successfully."));
    }

    // Kiểm tra người dùng có collecion đó chưa
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Object>> checkCollectionExists(
            @AuthenticationPrincipal User user,
            @RequestParam String collectionName
    ) {
        boolean exists = collectionService.collectionExists(user.getUserId(), collectionName);
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("exists", exists)));
    }
}