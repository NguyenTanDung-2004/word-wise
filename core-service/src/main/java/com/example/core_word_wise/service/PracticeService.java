package com.example.core_word_wise.service;

import com.example.core_word_wise.dto.practice.*;
import com.example.core_word_wise.entity.*;
import com.example.core_word_wise.entity.Collection;
import com.example.core_word_wise.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PracticeService {

    private final UserWordRepository userWordRepository;
    private final LearningStreakRepository learningStreakRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;
    private final StreakService streakService;
    private final LearningSessionDetailRepository learningSessionDetailRepository;
    private final SavedCollectionRepository savedCollectionRepository;
    private final UserSettingRepository userSettingRepository;

    // Lấy danh sách từ cần học cho hôm nay
    @Transactional
    public PracticeSessionDTO getPracticeSessionForToday(User user, String collectionName) {
        LocalDate today = LocalDate.now();
        List<UserWord> wordsToPractice;
        boolean isDailyPractice = (collectionName == null || collectionName.isBlank());

        if (!isDailyPractice) {
            // Khi học theo collection cụ thể, logic vẫn chỉ lấy từ của user đó trong collection đó
            Collection collection = collectionRepository.findByNameAndUserAndIsDeletedFalse(collectionName, user)
                    .orElseThrow(() -> new EntityNotFoundException("Collection with name '" + collectionName + "' not found."));
            wordsToPractice = userWordRepository.findByUserAndCollection_CollectionIdAndNextReviewDateLessThanEqual(user, collection.getCollectionId(), today);
        } else {
            // Khi học chung, nó sẽ tự động lấy tất cả các từ cần học của user,
            // bao gồm cả từ trong collection sở hữu và collection đã lưu (do đã được sao chép)
            wordsToPractice = userWordRepository.findByUserAndNextReviewDateLessThanEqual(user, today);
        }

        List<PracticeWordDTO> practiceWords = wordsToPractice.stream()
                .map(this::mapToPracticeWordDTO)
                .collect(Collectors.toList());

        StreakInfoDTO streakInfo = getStreakInfo(user);

        LearningSession session = new LearningSession();
        session.setSessionUuid(UUID.randomUUID().toString());
        session.setUser(user);
        session.setStatus(LearningSession.SessionStatus.PENDING);
        session.setIsDailyStreakSession(isDailyPractice);
        learningSessionRepository.save(session);

        PracticeSessionDTO.PracticeSessionDTOBuilder sessionBuilder = PracticeSessionDTO.builder()
                .sessionId(session.getSessionUuid())
                .userId(user.getUserId())
                .date(today)
                .streakInfo(streakInfo)
                .listWords(practiceWords);

        if (!isDailyPractice) {
            collectionRepository.findByNameAndUserAndIsDeletedFalse(collectionName, user)
                    .ifPresent(c -> sessionBuilder.collection(PracticeSessionDTO.CollectionInfoDTO.builder()
                            .collectionId(c.getCollectionId())
                            .collectionName(c.getName())
                            .totalWords(wordsToPractice.size())
                            .build()));
        }

        return sessionBuilder.build();
    }
    // Hoàn tất một phiên học đã được tạo trước
    @Transactional
    public CompletePracticeResponse completePracticeSession(User user, CompletePracticeRequest request) {
        LearningSession session = learningSessionRepository.findBySessionUuidAndStatus(request.getSessionId(), LearningSession.SessionStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or already completed session ID."));

        if (!session.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("User does not have permission to complete this session.");
        }

        session.setStatus(LearningSession.SessionStatus.COMPLETED);

        return processResultsAndUpdate(user, request.getResults(), session.getIsDailyStreakSession(), session);
    }

    // Hoàn tất một phiên học tùy chỉnh (người dùng tự chọn từ)
    @Transactional
    public CompletePracticeResponse completeCustomPracticeSession(User user, List<CompletePracticeRequest.WordResult> results) {
        LearningSession session = new LearningSession();
        session.setSessionUuid(UUID.randomUUID().toString());
        session.setUser(user);
        session.setIsDailyStreakSession(false);
        session.setStatus(LearningSession.SessionStatus.COMPLETED);

        return processResultsAndUpdate(user, results, false, session);
    }

    // Xử lý kết quả và cập nhật CSDL
    private CompletePracticeResponse processResultsAndUpdate(User user, List<CompletePracticeRequest.WordResult> results, boolean updateStreak, LearningSession session) {
        int correctCount = 0;
        int reviewTomorrowCount = 0;
        List<CompletePracticeResponse.WordUpdateDTO> wordUpdates = new ArrayList<>();
        List<LearningSessionDetail> sessionDetails = new ArrayList<>();

        Set<Integer> updatedCollectionIds = new HashSet<>();

        for (CompletePracticeRequest.WordResult result : results) {
            Optional<UserWord> userWordOpt = userWordRepository.findByUserAndWord_WordId(user, result.getWordId());

            if (userWordOpt.isEmpty()) continue;

            UserWord userWord = userWordOpt.get();

            float oldScore = userWord.getFamiliarityScore();
            boolean isCorrect = result.getLearnCount() > 0;
            float newScore = calculateNewScore(oldScore, result.getLearnCount());
            LocalDate nextReviewDate = calculateNextReviewDate(newScore);
            userWord.setFamiliarityScore(newScore);
            userWord.setNextReviewDate(nextReviewDate);
            userWord.setReviewCount(userWord.getReviewCount() + 1);

            userWordRepository.save(userWord);

            wordUpdates.add(CompletePracticeResponse.WordUpdateDTO.builder()
                    .wordId(result.getWordId()).previousScore(oldScore).newScore(newScore)
                    .nextReviewDate(nextReviewDate).needsReview(nextReviewDate.isBefore(LocalDate.now().plusDays(2)))
                    .build());

            sessionDetails.add(LearningSessionDetail.builder()
                    .session(session)
                    .word(userWord.getWord())
                    .previousScore(oldScore)
                    .newScore(newScore)
                    .isCorrect(isCorrect)
                    .build());
            updatedCollectionIds.add(userWord.getCollection().getCollectionId());
            if (isCorrect) correctCount++;
            if (nextReviewDate.isEqual(LocalDate.now().plusDays(1))) reviewTomorrowCount++;
        }

        StreakInfoDTO updatedStreakInfo = null;
        if (updateStreak) {
            LearningStreak updatedStreak = streakService.updateLearningStreak(user.getUserId());
            updatedStreakInfo = StreakInfoDTO.builder()
                    .currentStreakDays(updatedStreak.getCurrentStreakDays())
                    .longestStreakDays(updatedStreak.getLongestStreakDays())
                    .lastStudyDate(updatedStreak.getLastStudyDate())
                    .build();
        }
        if (!updatedCollectionIds.isEmpty()) {
            List<Collection> collectionsInSession = collectionRepository.findAllById(updatedCollectionIds);
            LocalDateTime now = LocalDateTime.now();

            for (Collection collection : collectionsInSession) {
                // Nếu người học là chủ sở hữu collection
                if (collection.getUser().getUserId().equals(user.getUserId())) {
                    collection.setLastStudiedAt(now);
                    collectionRepository.save(collection);
                }
                // Nếu người học là người đã save collection này
                else {
                    savedCollectionRepository.findByUserAndOriginalCollection(user, collection)
                            .ifPresent(savedCollection -> {
                                savedCollection.setLastStudiedAt(now);
                                savedCollectionRepository.save(savedCollection);
                            });
                }
            }
        }
        session.setTotalWords(results.size());
        session.setCorrectCount(correctCount);
        session.setIncorrectCount(results.size() - correctCount);
        learningSessionRepository.save(session);

        learningSessionDetailRepository.saveAll(sessionDetails);

        CompletePracticeResponse.SummaryDTO summary = CompletePracticeResponse.SummaryDTO.builder()
                .totalWords(results.size()).correct(correctCount)
                .incorrect(results.size() - correctCount).reviewTomorrow(reviewTomorrowCount)
                .build();

        return CompletePracticeResponse.builder()
                .message("Study session recorded successfully.")
                .updatedStreak(updatedStreakInfo)
                .wordsUpdate(wordUpdates)
                .summary(summary)
                .build();
    }

    // Lấy thông tin streak chỉ để đọc
    private StreakInfoDTO getStreakInfo(User user) {
        return learningStreakRepository.findById(user.getUserId())
                .map(streak -> StreakInfoDTO.builder()
                        .currentStreakDays(streak.getCurrentStreakDays())
                        .longestStreakDays(streak.getLongestStreakDays())
                        .lastStudyDate(streak.getLastStudyDate())
                        .build())
                .orElse(StreakInfoDTO.builder().currentStreakDays(0).longestStreakDays(0).lastStudyDate(null).build());
    }

    // Các hàm helper tính toán và ánh xạ
    private float calculateNewScore(float oldScore, int learnCount) {
        if (learnCount <= 0) { // Trường hợp trả lời sai
            return Math.max(0.0f, oldScore - 0.2f);
        }

        // Trường hợp trả lời đúng
        // Tăng điểm cơ bản là +0.15
        // Nhưng trừ đi một chút nếu phải học lại nhiều lần
        float penalty = (float) (Math.max(0, learnCount - 1) * 0.02); // Mỗi lần học lại (sau lần đầu) trừ 0.02
        float change = 0.15f - penalty;

        return Math.min(1.0f, oldScore + change);
    }
    private LocalDate calculateNextReviewDate(float newScore) {
        if (newScore < 0.3) return LocalDate.now().plusDays(1);
        if (newScore < 0.5) return LocalDate.now().plusDays(3);
        if (newScore < 0.7) return LocalDate.now().plusDays(7);
        if (newScore < 0.9) return LocalDate.now().plusDays(15);
        return LocalDate.now().plusDays(30);
    }

    private PracticeWordDTO mapToPracticeWordDTO(UserWord userWord) {
        Word word = userWord.getWord();
        return PracticeWordDTO.builder()
                .wordId(word.getWordId()).word(word.getWordText()).wordVn(word.getWordVn())
                .partOfSpeech(word.getPartOfSpeech()).definitionEn(word.getDefinitionEn())
                .definitionVi(word.getDefinitionVi()).source(word.getSourceUrl())
                .phonetics(toJsonNode(word.getPhonetics())).examples(toJsonNode(word.getExamples()))
                .idiomsCollocations(toJsonNode(word.getIdiomsCollocations())).synonyms(word.getSynonyms())
                .familiarityScore(userWord.getFamiliarityScore()).reviewCount(userWord.getReviewCount())
                .priorityLevel(userWord.getPriorityLevel().name()).build();
    }

    @Transactional(readOnly = true)
    public List<PracticeWordDTO> getWordsForExtension(User user) {
        UserSetting setting = userSettingRepository.findById(user.getUserId())
                .orElse(new UserSetting());
        int wordCount = setting.getWordsPerSession() != null ? setting.getWordsPerSession() : 15;

        Pageable pageable = PageRequest.of(0, wordCount);
        List<UserWord> words = userWordRepository.findWordsForExtensionPractice(user.getUserId(), pageable);

        return words.stream()
                .map(this::mapToPracticeWordDTO)
                .collect(Collectors.toList());
    }

    private JsonNode toJsonNode(String jsonString) {
        try {
            return (jsonString == null || jsonString.isBlank()) ? null : objectMapper.readTree(jsonString);
        } catch (Exception e) {
            return null;
        }
    }
}