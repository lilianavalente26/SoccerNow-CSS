package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pt.ul.fc.css.soccernow.entities.*;

import java.util.List;
import java.util.Optional;

public interface RefereeRepository extends JpaRepository<Referee, Long> {
    
	boolean existsById(Long refereeId);
	
	Optional<Referee> findById(Long refereeId);

	List<Referee> findByNameContainingIgnoreCase(String name);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Referee r WHERE r.userId = :id")
    Optional<Referee> findByIdForUpdate(@Param("id") Long id);
}