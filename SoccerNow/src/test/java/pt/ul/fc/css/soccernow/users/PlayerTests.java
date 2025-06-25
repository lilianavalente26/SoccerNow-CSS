package pt.ul.fc.css.soccernow.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.controllers.UserController;
import pt.ul.fc.css.soccernow.dto.PlayerDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPlayer;

@SpringBootTest
@Transactional
public class PlayerTests {
    
    @Autowired
    private UserController uc;
    
    @Autowired
    private PlayerRepository pr;
    
    @Test
    void registerPlayer_ValidRegistration() {
        PlayerDto ud = new PlayerDto();
        ud.setName("Denisao");
        ud.setPreferedPosition("DEFENDER");
        
        Long id = uc.registerPlayer(ud).getBody();
        PlayerDto registeredPlayer = uc.getPlayer(id).getBody();
        
        assertEquals("Denisao", registeredPlayer.getName());
        assertEquals("DEFENDER", registeredPlayer.getPreferedPosition());
        assertTrue(pr.existsById(id));
    }
    
    @Test
    void registerPlayer_Empty() {
        PlayerDto ud = new PlayerDto();
        ud.setName("");
        ud.setPreferedPosition("GOALKEEPER");
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class, 
            () -> uc.registerPlayer(ud));
        assertEquals("Name can not be empty!", exception.getMessage());
    }
    
    @Test
    void registerPlayer_NullName() {
        PlayerDto ud = new PlayerDto();
        ud.setName(null);
        ud.setPreferedPosition("GOALKEEPER");
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class, 
            () -> uc.registerPlayer(ud));
        assertEquals("Name can not be empty!", exception.getMessage());
    }
    
    @Test
    void registerPlayer_BadPosition() {
        PlayerDto ud = new PlayerDto();
        ud.setName("Denisao");
        ud.setPreferedPosition("FRONTLINER");
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.registerPlayer(ud)
        );
        assertEquals("Player invalid position!", exception.getMessage());
    }
    
    @Test
    void registerPlayer_LowerCasePosition() {
        PlayerDto ud = new PlayerDto();
        ud.setName("LowerMan");
        ud.setPreferedPosition("defender");
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.registerPlayer(ud)
        );
        assertEquals("Player invalid position!", exception.getMessage());
    }
    
    @Test
    void registerPlayer_AllValidPositions() {
        String[] pos = {"GOALKEEPER", "DEFENDER", "MIDFIELDER", "FORWARD"};
        
        for (String ps : pos) {
            PlayerDto ud = new PlayerDto();
            ud.setName("player " + ps);
            ud.setPreferedPosition(ps);
            
            Long id = uc.registerPlayer(ud).getBody();
            PlayerDto p = uc.getPlayer(id).getBody();
            assertEquals(ps, p.getPreferedPosition());
        }
    }
    
    @Test
    void verifyPlayer_ValidPlayer() {
        PlayerDto ud = new PlayerDto();
        ud.setName("Liliana");
        ud.setPreferedPosition("FORWARD");
        Long id = uc.registerPlayer(ud).getBody();
        
        PlayerDto result = uc.getPlayer(id).getBody();
        
        assertEquals("Liliana", result.getName());
        assertEquals("FORWARD", result.getPreferedPosition());
    }
    
    @Test
    void verifyPlayer_NonExistentPlayer() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.getPlayer(9999L)
        );
        assertEquals("Player with Id 9999 does not exists!", exception.getMessage());
    }
    
    @Test
    void deletePlayer_RemovePlayer() {
        PlayerDto ud = new PlayerDto();
        ud.setName("Luis");
        ud.setPreferedPosition("GOALKEEPER");
        Long pId = uc.registerPlayer(ud).getBody();
        
        String response = uc.deletePlayer(pId).getBody();
        
        assertEquals("Player with ID " + pId + " removed!", response);
        assertFalse(pr.existsById(pId));
    }
    
    @Test
    void deletePlayer_NonExistentPlayer() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.deletePlayer(9999L)
        );
        assertEquals("Player with Id 9999 does not exists!", exception.getMessage());
    }
    
    @Test
    void updatePlayer_ValidUpdate() {
        PlayerDto ud = new PlayerDto();
        ud.setName("Old Name");
        ud.setPreferedPosition("DEFENDER");
        Long id = uc.registerPlayer(ud).getBody();
        
        PlayerDto updateData = new PlayerDto();
        updateData.setName("New Name");
        updateData.setPreferedPosition("FORWARD");
        uc.updatePlayer(id, updateData);
        
        PlayerDto updated = uc.getPlayer(id).getBody();
        assertEquals("New Name", updated.getName());
        assertEquals("FORWARD", updated.getPreferedPosition());
    }
    
 // -------------------- FILTER TESTS --------------------
    
    @Test
    void filterPlayersByName_ValidName() {
        PlayerDto ud1 = new PlayerDto();
        ud1.setName("Denisssao");
        ud1.setPreferedPosition("DEFENDER");
        Long id1 = uc.registerPlayer(ud1).getBody();

        PlayerDto ud2 = new PlayerDto();
        ud2.setName("Lilianaaa");
        ud2.setPreferedPosition("MIDFIELDER");
        uc.registerPlayer(ud2).getBody();

        List<ViewModelPlayer> results = uc.filterPlayersByName("Denisssao").getBody();
        assertEquals(1, results.size());
        assertEquals("Denisssao", results.get(0).getName());
    }
    
    @Test
    void filterPlayersByName_EmptyName() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.filterPlayersByName("")
        );
        assertEquals("Name cannot be empty!", exception.getMessage());
    }
    

	@Test
	void filterPlayersByGoalsScored_ValidRange() {
	    PlayerDto ud = new PlayerDto();
	    ud.setName("Luissss");
	    ud.setPreferedPosition("FORWARD");
	    Long id = uc.registerPlayer(ud).getBody();
	
	    List<ViewModelPlayer> results = uc.filterPlayersByGoalsScored(5, 0).getBody();
	    assertTrue(results.stream().anyMatch(p -> p.getName().equals("Luissss")));
	}
	
	@Test
	void filterPlayersByGoalsScored_InvalidRange() {
	    InvalidUserDataException exception = assertThrows(
	        InvalidUserDataException.class,
	        () -> uc.filterPlayersByGoalsScored(0, 5)
	    );
	    assertEquals("Minimum goals cannot be greater than maximum goals!", exception.getMessage());
	}
	
	@Test
	void filterPlayersByCardsReceived_ValidRange() {
	    PlayerDto ud = new PlayerDto();
	    ud.setName("CardsPlayer");
	    ud.setPreferedPosition("DEFENDER");
	    Long id = uc.registerPlayer(ud).getBody();

	    List<ViewModelPlayer> results = uc.filterPlayersByCardsReceived(3, 0).getBody();
	    assertTrue(results.stream().anyMatch(p -> p.getName().equals("CardsPlayer")));
	}
	
	@Test
	void filterPlayersByCardsReceived_NegativeValue() {
	    InvalidUserDataException exception = assertThrows(
	        InvalidUserDataException.class,
	        () -> uc.filterPlayersByCardsReceived(-1, null)
	    );
	    assertEquals("Card count cannot be negative!", exception.getMessage());
	}

	@Test
	void filterPlayersByGamesPlayed_ValidRange() {
	    PlayerDto ud = new PlayerDto();
	    ud.setName("GamesPlayer");
	    ud.setPreferedPosition("MIDFIELDER");
	    Long id = uc.registerPlayer(ud).getBody();

	    List<ViewModelPlayer> results = uc.filterPlayersByGamesPlayed(10, 0).getBody();
	    assertTrue(results.stream().anyMatch(p -> p.getName().equals("GamesPlayer")));
	}

	@Test
	void findAllPlayers_ReturnsAll() {
	    int initialCount = uc.findAllPlayers().getBody().size();
	    
	    PlayerDto ud = new PlayerDto();
	    ud.setName("NewPlayer");
	    ud.setPreferedPosition("GOALKEEPER");
	    uc.registerPlayer(ud).getBody();

	    List<ViewModelPlayer> results = uc.findAllPlayers().getBody();
	    assertEquals(initialCount + 1, results.size());
	}
}