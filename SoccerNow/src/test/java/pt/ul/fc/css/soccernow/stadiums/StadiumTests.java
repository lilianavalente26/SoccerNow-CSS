package pt.ul.fc.css.soccernow.stadiums;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.controllers.StadiumController;
import pt.ul.fc.css.soccernow.exceptions.InvalidStadiumDataException;
import pt.ul.fc.css.soccernow.repositories.StadiumRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StadiumTests {

    @Autowired
    private StadiumController sc;
    
    @Autowired
    private StadiumRepository sr;

    @Test
    void registerStadium_ValidStadium() {
        String stadiumName = "Test Stadium";
        Long stadiumId = sc.registerStadium(stadiumName).getBody();

        assertNotNull(stadiumId);
        assertEquals(stadiumName, sc.getStadium(stadiumId).getBody());
        assertTrue(sr.existsById(stadiumId));
    }

    @Test
    void registerStadium_EmptyName() {
        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.registerStadium("")
        );
        assertEquals("Stadium name cannot be null or empty", exception.getMessage());
    }

    @Test
    void registerStadium_NullName() {
        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.registerStadium(null)
        );
        assertEquals("Stadium name cannot be null or empty", exception.getMessage());
    }

    @Test
    void registerStadium_StadiumAlreadyExists() {
        String stadiumName = "Duplicate Stadium";
        sc.registerStadium(stadiumName);

        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.registerStadium(stadiumName)
        );
        assertEquals("Stadium already exists in the database", exception.getMessage());
    }

    @Test
    void getStadium_ValidStadium() {
        String stadiumName = "Get Test Stadium";
        Long stadiumId = sc.registerStadium(stadiumName).getBody();

        String retrievedName = sc.getStadium(stadiumId).getBody();
        assertEquals(stadiumName, retrievedName);
    }

    @Test
    void getStadium_NonExistentStadium() {
        Long nonExistentId = 9999L;

        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.getStadium(nonExistentId)
        );
        assertEquals("Stadium does not exist in the database", exception.getMessage());
    }

    @Test
    void deleteStadium_ValidStadium() {
        String stadiumName = "Delete Test Stadium";
        Long stadiumId = sc.registerStadium(stadiumName).getBody();

        String response = sc.deleteStadium(stadiumId).getBody();
        assertEquals("Stadium with ID " + stadiumId + " removed!", response);
        assertFalse(sr.existsById(stadiumId));
    }

    @Test
    void deleteStadium_NonExistentStadium() {
        Long nonExistentId = 9999L;

        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.deleteStadium(nonExistentId)
        );
        assertEquals("Stadium does not exist in the database", exception.getMessage());
    }

    @Test
    void deleteStadium_NullId() {
        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.deleteStadium(null)
        );
        assertEquals("Stadium ID cannot be null", exception.getMessage());
    }

    @Test
    void getStadium_NullId() {
        InvalidStadiumDataException exception = assertThrows(
            InvalidStadiumDataException.class,
            () -> sc.getStadium(null)
        );
        assertEquals("Stadium ID cannot be null", exception.getMessage());
    }
}