package pt.ul.fc.css.soccernow.tournaments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.controllers.TournamentController;
import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidTournamentDataException;

@SpringBootTest
@Transactional
public class TournamentTests {
	
	@Autowired
	private TournamentController tc;
	
	private TournamentDto validTDto;
	private TournamentDto invalidTDto;
	private TournamentDto minimalClubsTDto;
	
	@BeforeEach
	public void setup() {
		// Valid tournament
		validTDto = new TournamentDto();
		validTDto.setTournamentName("O Melhor Torneio");
		validTDto.setClubs(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)));
		validTDto.setOver(false);
		
		// Invalid tournament with empty name and not enough clubs!
		invalidTDto = new TournamentDto();
		invalidTDto.setTournamentName("");
		invalidTDto.setClubs(new ArrayList<>(List.of(1L, 2L, 3L)));
		invalidTDto.setOver(false);
		
		// Minimal clubs
		minimalClubsTDto = new TournamentDto();
		minimalClubsTDto.setTournamentName("Torneio Simples");
		minimalClubsTDto.setClubs(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)));
		minimalClubsTDto.setOver(false);
	}
	
	@Test
	public void testRegisterValidTournament() {
		ResponseEntity<Long> r = tc.registerTournament(validTDto);
		assertNotNull(r.getBody());
		assertTrue(r.getBody() > 0);
	}
	
	@Test
    public void testRegisterInvalidTournament_EmptyName() {
		invalidTDto.setClubs(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)));
        assertThrows(InvalidTournamentDataException.class, () -> {
            tc.registerTournament(invalidTDto);
        });
    }
	
    @Test
    public void testRegisterInvalidTournament_NotEnoughClubs() {
    	invalidTDto.setTournamentName("Invalid Clubs Count");
        assertThrows(InvalidTournamentDataException.class, () -> {
            tc.registerTournament(invalidTDto);
        });
    }
    
    @Test
    public void testRegisterTournamentWithMatches() {
        TournamentDto withMatchesDto = new TournamentDto();
        withMatchesDto.setTournamentName("Tournament With Matches");
        withMatchesDto.setClubs(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)));
        withMatchesDto.setMatches(new ArrayList<>(List.of(1L, 2L)));
        withMatchesDto.setOver(false);

        ResponseEntity<Long> r = tc.registerTournament(withMatchesDto);
        assertNotNull(r.getBody());
        assertTrue(r.getBody() > 0);
    }
    
    @Test
    public void testGetExistingTournament() {
        Long tId = tc.registerTournament(validTDto).getBody();
        
        ResponseEntity<TournamentDto> r = tc.getChampionship(tId);
        TournamentDto t = r.getBody();

        assertNotNull(t);
        assertEquals("O Melhor Torneio", t.getTournamentName());
        assertEquals(8, t.getClubs().size());
        assertFalse(t.isOver());
        
        assertEquals(validTDto.getTournamentName(), t.getTournamentName());
        assertEquals(validTDto.getClubs().size(), t.getClubs().size());
    }
    
    @Test
    public void testGetNonExistingTournament() {
        assertThrows(InvalidTournamentDataException.class, () -> {
            tc.getChampionship(9999L);
        });
    }
    
    @Test
    public void testDeleteTournamentWithoutMatches() {
        Long tId = tc.registerTournament(minimalClubsTDto).getBody();
        ResponseEntity<String> response = tc.deleteTournament(tId);
        assertEquals("Tournament with Id " + tId + " removed!", response.getBody());
        
        assertThrows(InvalidTournamentDataException.class, () -> {
            tc.getChampionship(tId);
        });
    }
    
    @Test
    public void testUpdateTournamentName() {
        Long tId = tc.registerTournament(validTDto).getBody();
        String newName = "Updated Tournament Name";
        
        ResponseEntity<String> response = tc.updateTournamentName(tId, newName);
        assertEquals("Tournament with Id " + tId + " updated!", response.getBody());
        
        TournamentDto updatedTournament = tc.getChampionship(tId).getBody();
        assertEquals(newName, updatedTournament.getTournamentName());
    }
    
    @Test
    public void testSetTournamentAsFinished() {
        Long tId = tc.registerTournament(validTDto).getBody();
        
        ResponseEntity<String> response = tc.updateTournamentIsOver(tId);
        assertEquals("Tournament with Id " + tId + " updated!", response.getBody());
        
        TournamentDto updatedTournament = tc.getChampionship(tId).getBody();
        assertTrue(updatedTournament.isOver());
    }
    
    @Test
    public void testAddClubToTournament() {
        Long tId = tc.registerTournament(minimalClubsTDto).getBody();
        
        ResponseEntity<String> response = tc.updateTournamentClubs(tId, 9L, null);
        assertEquals("Tournament with Id " + tId + " updated!", response.getBody());
        
        TournamentDto updatedTournament = tc.getChampionship(tId).getBody();
        assertTrue(updatedTournament.getClubs().contains(9L));
    }
    
    @Test
    public void testRemoveClubFromTournament() {
        Long tId = tc.registerTournament(minimalClubsTDto).getBody();
        
        assertThrows(InvalidTournamentDataException.class, () -> {
        	tc.updateTournamentClubs(tId, null, 1L);
        });
        
        TournamentDto updatedTournament = tc.getChampionship(tId).getBody();
        assertEquals(8, updatedTournament.getClubs().size());
        assertTrue(updatedTournament.getClubs().contains(1L));
    }
    
    @Test
    public void testSearchTournamentByName() {
        tc.registerTournament(validTDto);
        
        ResponseEntity<List<TournamentDto>> response = tc.searchTournamentByName("Melhor");
        
        List<TournamentDto> t = response.getBody();
        assertNotNull(t);
        assertFalse(t.isEmpty(), "Should find at least one tournament");
        
        assertFalse(response.getBody().isEmpty());
        assertTrue(response.getBody().stream()
            .anyMatch(tt -> tt.getTournamentName().equals("O Melhor Torneio")));
    }
    
    @Test
    public void testSearchTournamentByStatus() {
        tc.registerTournament(validTDto);
        // Search ongoing tournaments
        ResponseEntity<List<TournamentDto>> ongoing = tc.searchChampionshipByStatus(false);
        assertFalse(ongoing.getBody().isEmpty());
        // Search finished tournaments
        ResponseEntity<List<TournamentDto>> finished = tc.searchChampionshipByStatus(true);
        assertTrue(finished.getBody().isEmpty());
    }
    
    @Test
    public void testGetTournamentStandings() {
        Long tournamentId = tc.registerTournament(validTDto).getBody();
        ResponseEntity<String> response = tc.getTournamentStandings(tournamentId);
        
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Points: 0"));
        assertTrue(response.getBody().contains("Goals Scored: 0"));
        assertTrue(response.getBody().contains("Goals Conceded: 0"));
    }
}
