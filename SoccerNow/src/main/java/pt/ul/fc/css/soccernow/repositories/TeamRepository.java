package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pt.ul.fc.css.soccernow.entities.*;

public interface TeamRepository extends JpaRepository<Team, Long> {
	
	Optional<Team> findById(Long teamId);
	
	List<Team> findByClub(Club club);
	
	@Query("SELECT t.teamId FROM Team t")
	List<Long> findAllIds();
	
	List<Team> findAllByClubClubId(Long clubId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Team t WHERE t.teamId = :id")
    Optional<Team> findByIdForUpdate(@Param("id") Long id);
}
