package pt.ul.fc.css.soccernow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ul.fc.css.soccernow.entities.Stadium;

import java.util.Optional;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    boolean existsById(Long stadiumId);

    Optional<Stadium> findById(Long stadiumId);

    boolean existsByStadiumName(String stadiumName);

}
