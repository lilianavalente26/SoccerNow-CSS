package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.MatchStatistics;

import java.util.Optional;

public interface MatchStatisticsRepository extends JpaRepository<MatchStatistics, Long> {

    Optional<MatchStatistics> findById(Long playerId);

}
