package pt.ul.fc.css.soccernow.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.entities.Stadium;
import pt.ul.fc.css.soccernow.exceptions.InvalidStadiumDataException;
import pt.ul.fc.css.soccernow.repositories.StadiumRepository;

@Service
public class StadiumHandler {

    private final StadiumRepository sr;

    /**
     * Constructs a StadiumService with the necessary repository.
     *
     * @param sr StadiumRepository for stadium operations
     */
    public StadiumHandler(StadiumRepository sr) {
        this.sr = sr;
    }

    /**
     * Registers a new stadium with the specified name.
     *
     * @param stadiumName the name of the stadium
     * @return the ID of the newly registered stadium
     * @throws InvalidStadiumDataException if the name is invalid or stadium exists
     */
    @Transactional
    public long handleRegisterStadium(String stadiumName) {
        if (stadiumName == null || stadiumName.isEmpty()) {
            throw new InvalidStadiumDataException("Stadium name cannot be null or empty");
        }
        if (sr.existsByStadiumName(stadiumName)) {
            throw new InvalidStadiumDataException("Stadium already exists in the database");
        }
        Stadium stadium = new Stadium();
        stadium.setStadiumName(stadiumName);
        sr.save(stadium);
        return stadium.getStadiumId();
    }

    /**
     * Retrieves the name of a stadium by its ID.
     *
     * @param stadiumId the ID of the stadium
     * @return the name of the stadium
     * @throws InvalidStadiumDataException if the ID is invalid or stadium doesn't exist
     */
    public String handleGetStadium(Long stadiumId) {
        if (stadiumId == null) {
            throw new InvalidStadiumDataException("Stadium ID cannot be null");
        }
        if (!sr.existsById(stadiumId)) {
            throw new InvalidStadiumDataException("Stadium does not exist in the database");
        }

        String stadiumName = sr.findById(stadiumId).get().getStadiumName();

        return stadiumName;
    }

    /**
     * Deletes a stadium with the specified ID.
     *
     * @param stadiumId the ID of the stadium to delete
     * @throws InvalidStadiumDataException if the ID is invalid or stadium doesn't exist
     */
    @Transactional
    public void handleDeleteStadium(Long stadiumId) {
        if (stadiumId == null) {
            throw new InvalidStadiumDataException("Stadium ID cannot be null");
        }
        if (!sr.existsById(stadiumId)) {
            throw new InvalidStadiumDataException("Stadium does not exist in the database");
        }
        sr.deleteById(stadiumId);
    }
}
