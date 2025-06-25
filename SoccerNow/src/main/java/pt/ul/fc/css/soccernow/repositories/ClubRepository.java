package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.Club;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.Tournament;

public interface ClubRepository extends JpaRepository<Club, Long> {
			
	Optional<Club> findById (Long clubId);
	
	@Query("SELECT t FROM Team t WHERE t.teamId = :teamId AND t.club IS NOT NULL")
	Optional<Team> findTeamWithClub(@Param("teamId") Long teamId);
		
}
