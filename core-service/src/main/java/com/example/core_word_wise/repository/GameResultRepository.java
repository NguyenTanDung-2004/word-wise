package com.example.core_word_wise.repository;

import com.example.core_word_wise.entity.GameResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Integer> {

    List<GameResult> findByGameMode_GameModeIdOrderByScoreDescTimeTakenSecondsAsc(Integer gameModeId, Pageable pageable);

    @Query("SELECT gr FROM GameResult gr WHERE gr.user.id = :userId AND gr.gameMode.id = :gameModeId ORDER BY gr.score DESC, gr.timeTakenSeconds ASC")
    List<GameResult> findTopResultForUser(Integer userId, Integer gameModeId, Pageable pageable);

    @Query(value = """
        SELECT gr.user_id AS userId, MAX(gr.score) AS maxScore
        FROM game_result gr
        WHERE gr.game_mode_id = :gameModeId
        GROUP BY gr.user_id
        ORDER BY maxScore DESC
        """, nativeQuery = true)
    List<BestScoreProjection> findMaxScoresByGameMode(@Param("gameModeId") Integer gameModeId, Pageable pageable);

    @Query(value = """
        SELECT gr FROM GameResult gr
        WHERE gr.gameMode.gameModeId = :gameModeId 
        AND gr.user.userId IN :userIds
        AND gr.score IN :scores
        ORDER BY gr.score DESC, 
                 CASE WHEN gr.timeTakenSeconds IS NULL THEN 1 ELSE 0 END, 
                 gr.timeTakenSeconds ASC
        """)
    List<GameResult> findBestResultsByScoreAndUser(
            @Param("gameModeId") Integer gameModeId,
            @Param("userIds") List<Integer> userIds,
            @Param("scores") List<Integer> scores
    );
}