package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pt.ul.fc.css.soccernow.entities.*;
import pt.ul.fc.css.soccernow.enums.Position;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
	
	boolean existsById(Long playerId);
	
	Optional<Player> findById(Long playerId);
	
	@Query("SELECT p.userId FROM Player p")
    List<Long> findAllIds();

	List<Player> findByNameContainingIgnoreCase(String name);

	List<Player> findByPreferredPosition(Position position);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Player p WHERE p.userId = :id")
    Optional<Player> findByIdForUpdate(@Param("id") Long id);
}
