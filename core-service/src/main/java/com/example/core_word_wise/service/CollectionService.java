package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.collection.*;
import com.example.core_word_wise.entity.*;
import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserWordRepository userWordRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final ObjectMapper objectMapper;
    private final SavedCollectionRepository savedCollectionRepository;

    @Transactional(readOnly = true)
    public List<String> getCollectionNames(User user) {
        // 1. Lấy tên các collection tự tạo (Owned Collections)
        List<String> ownedNames = collectionRepository.findCollectionNamesByUserAndIsDeletedFalse(user);

        // 2. Lấy tên các collection đã lưu (Saved Collections)
        List<String> savedNames = savedCollectionRepository.findSavedCollectionNamesByUser(user);

        // 3. Hợp nhất hai danh sách và loại bỏ trùng lặp (dùng Set)
        Set<String> uniqueNames = new HashSet<>();
        uniqueNames.addAll(ownedNames);
        uniqueNames.addAll(savedNames);

        // 4. Trả về dưới dạng List
        return new ArrayList<>(uniqueNames);
    }

    @Transactional(readOnly = true)
    public boolean collectionExists(Integer userId, String collectionName) {
        // Tìm user từ userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Sử dụng phương thức repository đã tạo
        return collectionRepository.existsByNameAndUserAndIsDeletedFalse(collectionName, user);
    }

    @Transactional(readOnly = true)
    public List<CollectionSummaryDTO> getAllUserCollections(User user) {
        // 1. Lấy các collection tự tạo (OC)
        List<Collection> ownedCollections = collectionRepository.findAllByUserAndIsDeletedFalse(user);
        List<CollectionSummaryDTO> result = ownedCollections.stream()
                .map(collection -> mapToCollectionSummaryDTO(collection, collection.getLastStudiedAt(), user))
                .collect(Collectors.toList());

        // 2. Lấy các collection người dùng đã lưu (SC)
        List<SavedCollection> savedEntries = savedCollectionRepository.findByUser(user);
        List<CollectionSummaryDTO> savedDtos = savedEntries.stream()
                // Sử dụng OriginalCollection để lấy ID, nhưng đếm từ vựng của Current User
                .map(savedEntry -> mapToCollectionSummaryDTO(savedEntry.getOriginalCollection(), savedEntry.getLastStudiedAt(), user))
                .toList();

        result.addAll(savedDtos);
        return result;
    }

    private CollectionSummaryDTO mapToCollectionSummaryDTO(Collection collection, LocalDateTime lastStudied, User currentUser) {
        Long wordCount = userWordRepository.countByUserAndCollection_CollectionId(currentUser, collection.getCollectionId());

        return CollectionSummaryDTO.builder()
                .id(collection.getCollectionId())
                .name(collection.getName())
                .wordCount(wordCount)
                .lastStudied(formatLastStudied(lastStudied))
                .build();
    }

    private String formatLastStudied(LocalDateTime lastStudied) {
        if (lastStudied == null) {
            return "Never";
        }
        Duration duration = Duration.between(lastStudied, LocalDateTime.now());
        long days = duration.toDays();
        if (days == 0) return "Today";
        if (days < 30) return days + "d ago";
        return (days / 30) + "m ago";
    }

    @Transactional
    public CollectionResponseDTO createCollection(User user, CollectionRequest request) {
        collectionRepository.findByNameAndUser(request.getName(), user)
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Collection with name '" + request.getName() + "' already exists.");
                });

        Collection newCollection = new Collection();
        newCollection.setUser(user);
        newCollection.setName(request.getName());
        newCollection.setDescription(request.getDescription());
        newCollection.setIsPublic(false);
        newCollection.setCreatedAt(LocalDateTime.now());
        newCollection.setIsDeleted(false);

        Collection savedCollection = collectionRepository.save(newCollection);
        return mapToCollectionResponseDTO(savedCollection);
    }

    @Transactional
    public CollectionResponseDTO updateCollection(Integer collectionId, User user, CollectionRequest request) {
        Collection collection = collectionRepository.findByCollectionIdAndIsDeletedFalse(collectionId)
                .orElseThrow(() -> new EntityNotFoundException("Collection not found with id: " + collectionId));

        if (!collection.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("User does not have permission to update this collection.");
        }

        if (request.getName() != null && !request.getName().isBlank() && !request.getName().equals(collection.getName())) {
            if (collectionRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Collection name " + request.getName() + "' is already taken.");
            }
            collection.setName(request.getName());
        }


        if (request.getDescription() != null) {
            collection.setDescription(request.getDescription());
        }

        Collection updatedCollection = collectionRepository.save(collection);
        return mapToCollectionResponseDTO(updatedCollection);
    }


    @Transactional
    public void softDeleteCollection(Integer collectionId, User user) {
        Collection collection = collectionRepository.findByCollectionIdAndIsDeletedFalse(collectionId)
                .orElseThrow(() -> new EntityNotFoundException("Collection not found with id: " + collectionId));

        if (!collection.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("User does not have permission to delete this collection.");
        }

        collection.setIsDeleted(true);
        collectionRepository.save(collection);
    }

    @Transactional(readOnly = true)
    public CollectionDetailDTO getCollectionDetailsByName(String collectionName, User user) {
        Collection collection = collectionRepository.findByNameAndUserAndIsDeletedFalse(collectionName, user)
                .orElse(null);

        // Nếu không phải là collection gốc, kiểm tra Saved Collection
        if (collection == null) {
            SavedCollection savedEntry = savedCollectionRepository.findByUserAndOriginalCollection_Name(user, collectionName)
                    .orElseThrow(() -> new EntityNotFoundException("Collection '" + collectionName + "' not found."));
            collection = savedEntry.getOriginalCollection();
        }

        // Lấy danh sách các bản ghi UserWord thuộc về NGƯỜI DÙNG HIỆN TẠI và collection này
        List<UserWord> userWordsInCollection = userWordRepository.findAllByUserAndCollection_CollectionId(user, collection.getCollectionId());

        // Ánh xạ các bản ghi này sang DTO chi tiết của từ vựng
        List<WordDetailDTO> wordDetailDTOs = userWordsInCollection.stream()
                .map(userWord -> mapToWordDetailDTO(userWord.getWord()))
                // Dùng distinct() nếu có khả năng user vô tình lưu một từ 2 lần (dù logic add đã ngăn chặn)
                .collect(Collectors.toList());

        return CollectionDetailDTO.builder()
                .collectionId(collection.getCollectionId())
                .name(collection.getName())
                .description(collection.getDescription())
                .totalWords((long) wordDetailDTOs.size()) // Tổng số từ là số từ user này đã lưu
                .createdAt(collection.getCreatedAt())
                .lastStudiedAt(collection.getLastStudiedAt())
                .words(wordDetailDTOs)
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

    private CollectionResponseDTO mapToCollectionResponseDTO(Collection collection) {
        return CollectionResponseDTO.builder()
                .collectionId(collection.getCollectionId())
                .userId(collection.getUser().getUserId())
                .name(collection.getName())
                .description(collection.getDescription())
                .isPublic(collection.getIsPublic())
                .createdAt(collection.getCreatedAt())
                .build();
    }


    // ADD WORD
    @Transactional
    public AddWordResponseDTO addWordsToCollection(User authenticatedUser, AddWordRequest request) {
        User user = (authenticatedUser != null) ? authenticatedUser :
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found for the provided userId."));

        Collection collection = findOrCreateCollectionForAddingWords(user, request.getCollectionName());

        List<String> successList = new ArrayList<>();
        List<AddWordResponseDTO.FailedWordDTO> failedList = new ArrayList<>();

        for (WordDTO wordDto : request.getWords()) {
            try {
                Word word = findOrCreateWord(wordDto);

                if (userWordRepository.existsByUserAndWordAndCollection(user, word, collection)) {
                    // Nếu đã tồn tại, coi như thất bại
                    failedList.add(new AddWordResponseDTO.FailedWordDTO(word.getWordText(), "Already exists in this collection."));
                    continue; // Bỏ qua và xử lý từ tiếp theo
                }

                UserWord userWord = new UserWord();
                userWord.setUser(user);
                userWord.setWord(word);
                userWord.setCollection(collection);
                userWord.setFamiliarityScore(0.0f);
                userWord.setReviewCount(0);
                userWord.setNextReviewDate(LocalDate.now());
                userWord.setPriorityLevel(UserWord.PriorityLevel.MEDIUM);

                userWordRepository.save(userWord);

                successList.add(word.getWordText());

            } catch (Exception e) {
                failedList.add(new AddWordResponseDTO.FailedWordDTO(wordDto.getWord(), e.getMessage()));
            }
        }

        return AddWordResponseDTO.builder()
                .success(successList)
                .failed(failedList)
                .build();
    }

    private Collection findOrCreateCollectionForAddingWords(User user, String collectionName) {
        return collectionRepository.findByNameAndUserAndIsDeletedFalse(collectionName, user)
                .orElseGet(() -> {
                    Collection newCollection = new Collection();
                    newCollection.setUser(user);
                    newCollection.setName(collectionName);
                    newCollection.setIsPublic(false);
                    newCollection.setIsDeleted(false);
                    return collectionRepository.save(newCollection);
                });
    }


    private Word findOrCreateWord(WordDTO wordDto) {
        return wordRepository.findByWordText(wordDto.getWord())
                .orElseGet(() -> {
                    Word newWord = new Word();
                    newWord.setWordText(wordDto.getWord());
                    newWord.setWordVn(wordDto.getWord_vn());
                    newWord.setPartOfSpeech(wordDto.getPartOfSpeech());
                    newWord.setDefinitionEn(wordDto.getDefinition_en());
                    newWord.setDefinitionVi(wordDto.getDefinition_vi());
                    newWord.setSourceUrl(wordDto.getSource());

                    try {
                        newWord.setPhonetics(objectMapper.writeValueAsString(wordDto.getPhonetics()));
                        newWord.setExamples(objectMapper.writeValueAsString(wordDto.getExamples()));
                        newWord.setIdiomsCollocations(objectMapper.writeValueAsString(wordDto.getIdioms_collocations()));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to serialize word details to JSON", e);
                    }

                    if (wordDto.getSynonyms() != null) {
                        newWord.setSynonyms(String.join(",", wordDto.getSynonyms()));
                    }

                    return wordRepository.save(newWord);
                });
    }

    private Collection findOrCreateCollection(User user, String collectionName) {
        Optional<Collection> existingCollection = collectionRepository.findByNameAndUserAndIsDeletedFalse(collectionName, user);

        // Nếu đã tồn tại, ném lỗi để người dùng biết
        if (existingCollection.isPresent()) {
            throw new IllegalArgumentException("Collection with name '" + collectionName + "' already exists.");
        }

        // Nếu không, tạo mới
        Collection newCollection = new Collection();
        newCollection.setUser(user);
        newCollection.setName(collectionName);
        newCollection.setIsPublic(false);
        newCollection.setIsDeleted(false);
        return collectionRepository.save(newCollection);
    }


    @Transactional
    public CollectionResponseDTO createCollectionWithWords(User user, CreateCollectionWithWordsRequest request) {
        // Tạo collection mới
        Collection newCollection = findOrCreateCollection(user, request.getCollectionName());

        // Cập nhật description nếu có
        if (request.getDescription() != null) {
            newCollection.setDescription(request.getDescription());
        }

        // Lặp qua danh sách từ và thêm vào collection
        for (WordDTO wordDto : request.getWords()) {
            // Tái sử dụng logic tìm hoặc tạo Word
            Word word = findOrCreateWord(wordDto);

            // Kiểm tra xem từ đã có trong collection chưa để tránh lỗi
            if (!userWordRepository.existsByUserAndWordAndCollection(user, word, newCollection)) {
                UserWord userWord = new UserWord();
                userWord.setUser(user);
                userWord.setWord(word);
                userWord.setCollection(newCollection);
                userWord.setFamiliarityScore(0.0f);
                userWord.setReviewCount(0);
                userWord.setNextReviewDate(LocalDate.now());
                userWord.setPriorityLevel(UserWord.PriorityLevel.MEDIUM);

                userWordRepository.save(userWord);
            }
        }

        // Trả về DTO của collection vừa được tạo
        return mapToCollectionResponseDTO(newCollection);
    }


    @Transactional
    public void removeWordFromCollection(Integer collectionId, Integer wordId, User user) {
        //Tìm bản ghi liên kết UserWord
        UserWord userWord = userWordRepository.findByUserAndWord_WordIdAndCollection_CollectionId(user, wordId, collectionId)
                .orElseThrow(() -> new EntityNotFoundException("Word not found in this collection."));

        //Xóa bản ghi UserWord (xóa liên kết)
        userWordRepository.delete(userWord);

        //Kiểm tra xem từ này có còn được sử dụng ở nơi nào khác không
        if (!userWordRepository.existsByWord_WordId(wordId)) {
            // Nếu không còn ai sử dụng từ này, xóa nó khỏi bảng `word`
            wordRepository.deleteById(wordId);
        }
    }
}