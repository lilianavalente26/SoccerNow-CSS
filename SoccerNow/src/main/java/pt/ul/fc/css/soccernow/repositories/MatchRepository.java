package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pt.ul.fc.css.soccernow.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
		
	Optional<Match> findById(Long matchId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Match m WHERE m.matchId = :id")
    Optional<Match> findByIdForUpdate(@Param("id") Long id);
	
	@Query("""
		SELECT COUNT(m) > 0
		FROM Match m
		WHERE (
			EXISTS (
				SELECT p FROM m.team1.players p WHERE p.userId = :playerId
			) OR EXISTS (
				SELECT p FROM m.team2.players p WHERE p.userId = :playerId
			)
		)
		AND (
			m.date > :today
			OR (m.date = :today AND m.time > :now)
		)
	""")
	boolean hasUpcomingMatchesPlayer(
	    @Param("playerId") Long playerId,
	    @Param("today") LocalDate today,
	    @Param("now") LocalTime now
	);
	
	@Query("""
	    SELECT COUNT(m) > 0
	    FROM Match m
	    WHERE EXISTS (
	        SELECT r FROM m.referees r WHERE r.userId = :refereeId
	    )
	    AND (
	        m.date > :today
	        OR (m.date = :today AND m.time > :now)
	    )
	""")
	boolean hasUpcomingMatchesReferee(
	    @Param("refereeId") Long refereeId,
	    @Param("today") LocalDate today,
	    @Param("now") LocalTime now
	);
	
	@Query("""
	    SELECT COUNT(m) > 0
	    FROM Match m
	    WHERE (
	        m.team1.teamId IN :teamIds OR m.team2.teamId IN :teamIds
	    )
	    AND (
	        m.date > :today
	        OR (m.date = :today AND m.time > :now)
	    )
	""")
	boolean upcomingMatchesForTeams(
	    @Param("teamIds") List<Long> teamIds,
	    @Param("today") LocalDate today,
	    @Param("now") LocalTime now
	);
	
	@Query("""
	    SELECT COUNT(m) > 0
	    FROM Match m
	    WHERE m.matchId IN :matchIds
	    AND (
	        m.date > :today
	        OR (m.date = :today AND m.time > :now)
	    )
	""")
	boolean hasUpcomingMatchesFromIds(
	    @Param("matchIds") List<Long> matchIds,
	    @Param("today") LocalDate today,
	    @Param("now") LocalTime now
	);
	
	
	@Query("""
	    SELECT m.matchId FROM Match m
	    WHERE 
	        (m.team1.club.clubId = :clubId OR m.team2.club.clubId = :clubId)
	        AND m.stats.isOver = true
	    """)
	List<Long> findFinishedMatchIdsByClubId(@Param("clubId") Long clubId);
	
	@Query("""
	    SELECT COUNT(m) FROM Match m
	    WHERE ((m.team1.club.clubId = :clubId AND m.stats.winnerTeam = 1)
	       OR (m.team2.club.clubId = :clubId AND m.stats.winnerTeam = 2))
	      AND m.stats.isOver = true
	""")
	int countWinsByClub(@Param("clubId") Long clubId);
	
	@Query("""
	    SELECT COUNT(m) FROM Match m
	    WHERE ((m.team1.club.clubId = :clubId AND m.stats.winnerTeam = 2)
	       OR (m.team2.club.clubId = :clubId AND m.stats.winnerTeam = 1))
	    AND m.stats.isOver = true
	""")
	int countLossesByClub(@Param("clubId") Long clubId);

	@Query("""
	    SELECT COUNT(m) FROM Match m
	    WHERE (m.team1.club.clubId = :clubId OR m.team2.club.clubId = :clubId)
	    AND m.stats.isOver = true
	    AND m.stats.winnerTeam IS NULL
	""")
	int countDrawsByClub(@Param("clubId") Long clubId);
	
}
