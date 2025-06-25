package pt.ul.fc.css.soccernow.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.ul.fc.css.soccernow.entities.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
	
	Optional<Achievement> findById(Long id);
	
}
