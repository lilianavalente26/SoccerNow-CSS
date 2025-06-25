package pt.ul.fc.css.soccernow.clubs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.controllers.ClubController;
import pt.ul.fc.css.soccernow.dto.ClubDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidClubDataException;
import pt.ul.fc.css.soccernow.repositories.ClubRepository;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelClub;

@SpringBootTest
@Transactional
public class ClubTests {

    @Autowired
    private ClubController cc;
    
    @Autowired
    private ClubRepository cr;

    @Test
    void registerClub_ValidRegistration() {
        String clubName = "Club1";
        Long clubId = cc.registerClub(clubName).getBody();
        
        ClubDto registeredClub = cc.getClub(clubId).getBody();
        assertEquals(clubName, registeredClub.getName());
        assertTrue(cr.existsById(clubId));
    }

    @Test
    void registerClub_EmptyName() {
        InvalidClubDataException exception = assertThrows(
            InvalidClubDataException.class,
            () -> cc.registerClub("")
        );
        assertEquals("Name can not be empty!", exception.getMessage());
    }

    @Test
    void getClub_ValidClub() {
        String clubName = "Existing Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        ClubDto result = cc.getClub(clubId).getBody();
        assertEquals(clubName, result.getName());
    }

    @Test
    void getClub_NonExistentClub() {
        InvalidClubDataException exception = assertThrows(
            InvalidClubDataException.class,
            () -> cc.getClub(9999L)
        );
        assertEquals("Club with Id 9999 does not exist!", exception.getMessage());
    }

    @Test
    void deleteClub_ValidClub() {
        String clubName = "DeadClub";
        Long clubId = cc.registerClub(clubName).getBody();
        
        String response = cc.deletePlayer(clubId).getBody();
        assertEquals("Club with ID " + clubId + " removed!", response);
        assertFalse(cr.existsById(clubId));
    }

    @Test
    void deleteClub_NonExistentClub() {
        InvalidClubDataException exception = assertThrows(
            InvalidClubDataException.class,
            () -> cc.deletePlayer(9999L)
        );
        assertEquals("Club with Id 9999 does not exist!", exception.getMessage());
    }

    @Test
    void getMatchHistory_ValidClub() {
        String clubName = "HistoryClub";
        Long clubId = cc.registerClub(clubName).getBody();
        
        String history = cc.getMatchHistory(clubId).getBody();
        assertTrue(history.contains("MatchHistory:"));
    }

    @Test
    void getMatchHistory_NonExistentClub() {
        InvalidClubDataException exception = assertThrows(
            InvalidClubDataException.class,
            () -> cc.getMatchHistory(9999L)
        );
        assertEquals("Club with Id 9999 does not exist!", exception.getMessage());
    }

    @Test
    void getAchievements_ValidClub() {
        String clubName = "AchievementsClub";
        Long clubId = cc.registerClub(clubName).getBody();
        
        String achievements = cc.getAchievements(clubId).getBody();
        assertTrue(achievements.contains("Club has no achievements!") || 
                 achievements.contains("Tournament:"));
    }

    @Test
    void getAchievements_NonExistentClub() {
        InvalidClubDataException exception = assertThrows(
            InvalidClubDataException.class,
            () -> cc.getAchievements(9999L)
        );
        assertEquals("Club with Id 9999 does not exist!", exception.getMessage());
    }

    @Test
    void searchFilterClub_ByName() {
        String clubName1 = "Filter Club 1";
        String clubName2 = "Filter Club 2";
        cc.registerClub(clubName1).getBody();
        cc.registerClub(clubName2).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(clubName1, null, null, null, null, null, null, null, null).getBody();
        assertEquals(1, results.size());
        assertEquals(clubName1, results.get(0).getName());
    }

    @Test
    void searchFilterClub_ByMinPlayers() {
        String expectedClubName = "Sporting Clube de Lisboa"; // From SoccerNowApplication.java

        List<ViewModelClub> results = cc.serchFilterClub(null, 1, null, null, null, null, null, null, null).getBody();

        assertTrue(results.stream().anyMatch(c -> expectedClubName.equals(c.getName())));
    }

    @Test
    void searchFilterClub_ByMaxPlayers() {
        String clubName = "Club with Few Players";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, 10, null, null, null, null, null, null).getBody();
        assertTrue(results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_ByNWins() {
        String clubName = "Winning Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, null, 0, null, null, null, null, null).getBody();
        assertTrue(results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_ByNDraws() {
        String clubName = "Drawing Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, null, null, 0, null, null, null, null).getBody();
        assertTrue(results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_ByNLosses() {
        String clubName = "Losing Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, null, null, null, 0, null, null, null).getBody();
        assertTrue(results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_ByNAchievements() {
        String clubName = "Achieving Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, null, null, null, null, 0, null, null).getBody();
        assertTrue(results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_ByAchievementPosition() {
        String clubName = "Position Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, null, null, null, null, null, 1, null).getBody();
        assertTrue(results.isEmpty() || results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_ByMissingPlayerPosition() {
        String clubName = "Position Missing Club";
        Long clubId = cc.registerClub(clubName).getBody();
        
        List<ViewModelClub> results = cc.serchFilterClub(null, null, null, null, null, null, null, null, "GOALKEEPER").getBody();

        assertTrue(results.isEmpty() || results.stream().anyMatch(c -> c.getName().equals(clubName)));
    }

    @Test
    void searchFilterClub_InvalidPlayerPosition() {
        InvalidClubDataException exception = assertThrows(
            InvalidClubDataException.class,
            () -> cc.serchFilterClub(null, null, null, null, null, null, null, null, "INVALID_POSITION")
        );
        assertEquals("Player invalid position!", exception.getMessage());
    }
}