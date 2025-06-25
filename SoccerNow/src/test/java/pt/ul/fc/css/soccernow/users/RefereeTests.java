package pt.ul.fc.css.soccernow.users;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.controllers.UserController;
import pt.ul.fc.css.soccernow.dto.RefereeDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.repositories.RefereeRepository;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelReferee;

@SpringBootTest
@Transactional
public class RefereeTests {

    @Autowired
    private UserController uc;
    
    @Autowired
    private RefereeRepository rr;

    @Test
    void registerReferee_ValidRegistration() {
        RefereeDto ud = new RefereeDto();
        ud.setName("Referee One");
        ud.setHasCertificate(true);
        
        Long id = uc.registerReferee(ud).getBody();
        RefereeDto registeredReferee = uc.getReferee(id).getBody();
        
        assertEquals("Referee One", registeredReferee.getName());
        assertTrue(registeredReferee.getHasCertificate());
        assertTrue(rr.existsById(id));
    }

    @Test
    void registerReferee_EmptyName() {
        RefereeDto ud = new RefereeDto();
        ud.setName("");
        ud.setHasCertificate(false);
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.registerReferee(ud)
        );
        assertEquals("Name can not be empty!", exception.getMessage());
    }

    @Test
    void registerReferee_NullName() {
        RefereeDto ud = new RefereeDto();
        ud.setName(null);
        ud.setHasCertificate(true);
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.registerReferee(ud)
        );
        assertEquals("Name can not be empty!", exception.getMessage());
    }

    @Test
    void verifyReferee_ValidReferee() {
        RefereeDto ud = new RefereeDto();
        ud.setName("Experienced Ref");
        ud.setHasCertificate(true);
        Long id = uc.registerReferee(ud).getBody();
        
        RefereeDto result = uc.getReferee(id).getBody();
        
        assertEquals("Experienced Ref", result.getName());
        assertTrue(result.getHasCertificate());
        assertTrue(result.getMatches().isEmpty());
    }

    @Test
    void verifyReferee_NonExistentReferee() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.getReferee(9999L)
        );
        assertEquals("Referee with Id 9999 does not exists!", exception.getMessage());
    }

    @Test
    void deleteReferee_RemoveReferee() {
        RefereeDto ud = new RefereeDto();
        ud.setName("To Be Removed");
        ud.setHasCertificate(false);
        Long rId = uc.registerReferee(ud).getBody();
        
        String response = uc.deleteReferee(rId).getBody();
        
        assertEquals("Referee with ID " + rId + " removed!", response);
        assertFalse(rr.existsById(rId));
    }

    @Test
    void deleteReferee_NonExistentReferee() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.deleteReferee(9999L)
        );
        assertEquals("Referee with Id 9999 does not exists!", exception.getMessage());
    }

    @Test
    void updateReferee_ValidUpdate() {
        RefereeDto ud = new RefereeDto();
        ud.setName("Old Name");
        ud.setHasCertificate(false);
        Long id = uc.registerReferee(ud).getBody();
        
        RefereeDto updateData = new RefereeDto();
        updateData.setName("New Name");
        updateData.setHasCertificate(true);
        uc.updateReferee(id, updateData);
        
        RefereeDto updated = uc.getReferee(id).getBody();
        assertEquals("New Name", updated.getName());
        assertTrue(updated.getHasCertificate());
    }
    
 // -------------------- FILTER TESTS --------------------

    @Test
    void filterRefereesByName_ValidName() {
        RefereeDto ud1 = new RefereeDto();
        ud1.setName("FilterRef1");
        ud1.setHasCertificate(true);
        Long id1 = uc.registerReferee(ud1).getBody();

        RefereeDto ud2 = new RefereeDto();
        ud2.setName("Denis123");
        ud2.setHasCertificate(false);
        Long id2 = uc.registerReferee(ud2).getBody();

        List<ViewModelReferee> results = uc.filterRefereesByName("Denis123").getBody();
        assertEquals(1, results.size());
        assertEquals("Denis123", results.get(0).getName());
    }

    @Test
    void filterRefereesByName_EmptyName() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.filterRefereesByName("")
        );
        assertEquals("Name cannot be empty!", exception.getMessage());
    }

    @Test
    void filterRefereesByMatchesOficialized_ValidRange() {
        RefereeDto ud = new RefereeDto();
        ud.setName("MatchesRef");
        ud.setHasCertificate(true);
        Long id = uc.registerReferee(ud).getBody();

        List<ViewModelReferee> results = uc.filterRefereesByMatchesOficialized(5, 0).getBody();
        assertTrue(results.stream().anyMatch(r -> r.getName().equals("MatchesRef")));
    }

    @Test
    void filterRefereesByMatchesOficialized_InvalidRange() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.filterRefereesByMatchesOficialized(0, 5)
        );
        assertEquals("Minimum matches cannot be greater than maximum matches!", exception.getMessage());
    }

    @Test
    void filterRefereesByCardsShown_ValidRange() {
        RefereeDto ud = new RefereeDto();
        ud.setName("CardsRef");
        ud.setHasCertificate(true);
        Long id = uc.registerReferee(ud).getBody();

        List<ViewModelReferee> results = uc.filterRefereesByCardsShown(3, 0).getBody();
        assertTrue(results.stream().anyMatch(r -> r.getName().equals("CardsRef")));
    }

    @Test
    void filterRefereesByCardsShown_NegativeValue() {
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.filterRefereesByCardsShown(-1, null)
        );
        assertEquals("Cards shown cannot be negative!", exception.getMessage());
    }

    @Test
    void findAllReferees_ReturnsAll() {
        int initialCount = uc.findAllReferees().getBody().size();
        
        RefereeDto ud = new RefereeDto();
        ud.setName("NewReferee");
        ud.setHasCertificate(true);
        uc.registerReferee(ud).getBody();

        List<ViewModelReferee> results = uc.findAllReferees().getBody();
        assertEquals(initialCount + 1, results.size());
    }
}