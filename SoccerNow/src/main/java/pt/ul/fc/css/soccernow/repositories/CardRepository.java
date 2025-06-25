package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.ul.fc.css.soccernow.entities.Card;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.MatchStatistics;
import pt.ul.fc.css.soccernow.entities.Player;

public interface CardRepository extends JpaRepository<Card, Long> {

    void deleteByCardId(Long cardId);

    Card findByCardId(Long cardId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.player = :player")
    int countCardsByPlayer(@Param("player") Player player);

    int countCardsByMatchStatistics(MatchStatistics matchStatistics);
}
