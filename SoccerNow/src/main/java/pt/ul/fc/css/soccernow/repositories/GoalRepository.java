package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.ul.fc.css.soccernow.entities.Goal;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.Player;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByGoalId(Long goalId);

    void deleteByGoalId(Long goalId);

    List<Goal> findByPlayer(Player player);

    List<Goal> findByMatchStatistics_Match_MatchId(Long matchId);

    List<Goal> findByMatch(Match match);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.player = :player")
    int countGoalsByPlayer(@Param("player") Player player);
}
