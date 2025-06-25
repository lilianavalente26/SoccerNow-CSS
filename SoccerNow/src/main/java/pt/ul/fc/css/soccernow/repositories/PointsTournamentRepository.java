package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pt.ul.fc.css.soccernow.entities.*;

import java.util.List;
import java.util.Optional;

public interface PointsTournamentRepository extends JpaRepository<PointsTournament, Long>{
    @Override
    List<PointsTournament> findAll();

    boolean existsById(Long tournamentId);

    Optional<PointsTournament> findById(Long tournamentId);

    Optional<PointsTournament> findByTournamentName(String tournamentName);

    List<PointsTournament> findByIsOver(boolean isOver);

    List<PointsTournament> findByTournamentNameContainingIgnoreCase(String name);

    List<Tournament> findByClubs_ClubId(Long clubId);
    
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM PointsTournament t WHERE t.tournamentId = :id")
    Optional<PointsTournament> findByIdForUpdate(@Param("id") Long id);
}
