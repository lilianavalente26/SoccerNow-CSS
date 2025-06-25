package pt.ul.fc.css.soccernow.handlers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.dto.*;
import pt.ul.fc.css.soccernow.entities.Achievement;
import pt.ul.fc.css.soccernow.entities.Club;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.PointsTournament;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.enums.Position;
import pt.ul.fc.css.soccernow.exceptions.InvalidClubDataException;
import pt.ul.fc.css.soccernow.mappers.ClubMapper;
import pt.ul.fc.css.soccernow.repositories.*;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelClub;


@Service
public class ClubHandler {

	private final ClubRepository cr;
	private final TeamRepository tr;
	private final PointsTournamentRepository pointsTournamentRepository;
	private final MatchRepository mr;
	private final PlayerRepository pr;
	
    /**
     * Constructs a ClubService with the necessary repositories.
     *
     * @param cr ClubRepository for club operations
     * @param tr TeamRepository for team operations
     * @param mr MatchRepository for match operations
     * @param pr PlayerRepository for player operations
     */
	@Autowired
	public ClubHandler(ClubRepository cr, TeamRepository tr, MatchRepository mr, PlayerRepository pr,
					   PointsTournamentRepository pointsTournamentRepository) {
		this.cr = cr;
		this.mr = mr;
		this.tr = tr;
		this.pr = pr;
		this.pointsTournamentRepository = pointsTournamentRepository;
	}

    /**
     * Registers a new club with the provided data.
     *
     * @param name String containing club registration data
     * @return the ID of the newly registered club
     * @throws InvalidClubDataException if the club data is invalid
     */
	@Transactional
	public long handleRegisterClub(String name) {
		isValidName(name);
		Club club = ClubMapper.toClub(name);
		cr.save(club);
		return club.getClubId();
	}

	/**
	 * Retrieves a club by its ID.
	 * @param clubId the ID of the club to retrieve
	 * @return ClubDto containing club data
	 */
	public ClubDto handleGetClub(Long clubId) {
		Optional<Club> optionalClub = cr.findById(clubId);
		if (optionalClub.isPresent()) {
			return ClubMapper.toDto(optionalClub.get());
		} else {
			throw new InvalidClubDataException("Club with Id " + clubId + " does not exist!");
		}
	}
		
    /**
     * Deletes a club with the specified ID.
     *
     * @param clubId the ID of the club to delete
     * @throws InvalidClubDataException if the club doesn't exist or has future matches
     */
	@Transactional
	public void handleDeleteClub(Long clubId) {
		Optional<Club> optionalClub = cr.findById(clubId);
		if (optionalClub.isPresent()) {
			Club club = optionalClub.get();
			checkIfClubHasFutureMatches(club);
			removeTeamsFromClub(club);
			cr.delete(club);
		} else {
			throw new InvalidClubDataException("Club with Id " + clubId + " does not exist!");
		}
	}
	
	/**
	 * Determine the match history of the clubId 
	 * 
	 * @param clubId the id of the club
	 * @return a list of all matches the club already played in String format
	 */
	public String handleGetMatchHistory(Long clubId) {
		Optional<Club> optionalClub = cr.findById(clubId);
		if (optionalClub.isPresent()) {
			List<Long> matches = mr.findFinishedMatchIdsByClubId(clubId);
			return matches.toString();
		} else {
			throw new InvalidClubDataException("Club with Id " + clubId + " does not exist!");
		}
	}

	/**
	 * Retrieves the achievements of a club by its ID.
	 *
	 * @param clubId the ID of the club
	 * @return a String representation of the club's achievements
	 * @throws InvalidClubDataException if the club does not exist
	 */
	public String handleGetAchievements(Long clubId) {
		Optional<Club> optionalClub = cr.findById(clubId);
		if (optionalClub.isPresent()) {
			List<Achievement> achievements = optionalClub.get().getAchievements();
			StringBuilder sb = new StringBuilder();
			if (achievements.size() == 0) {
				sb.append("Club has no achievements!");
			}
			for (Achievement a: achievements) {
				sb.append("Tournament: " + a.getTournament().getTournamentName() + " | ");
				sb.append("Position: " + a.getPlacement() + "\n");
			}
			return sb.toString();
		} else {
			throw new InvalidClubDataException("Club with Id " + clubId + " does not exist!");
		}
	}
	
	/**
	 * Retrieves all clubs in a formatedDto
	 * 
	 * @return all clubs
	 */
	public List<ViewModelClub> handleGetAllClubs() {
		List<Club> clubs = cr.findAll();
		return clubs.stream().map(c -> ClubMapper.toFormatedDto(c)).collect(Collectors.toList());
	}

	/**
	 * Retrieves a list of clubs based on various search filters.
	 *
	 * @param name the name of the club (optional)
	 * @param minPlayers minimum number of players in the club (optional)
	 * @param maxPlayers maximum number of players in the club (optional)
	 * @param nWins number of wins (optional)
	 * @param nDraws number of draws (optional)
	 * @param nLosses number of losses (optional)
	 * @param nAchievements number of achievements (optional)
	 * @param achievementPosition position in achievements (optional)
	 * @param missingPlayerPosition position that should not be present in any team of the club (optional)
	 * @return a list of ClubDto objects matching the search criteria
	 */
	public List<ViewModelClub> handleSearchFilterClub(String name, Integer minPlayers, Integer maxPlayers, Integer nWins, 
			Integer nDraws, Integer nLosses, Integer nAchievements, Integer achievementPosition, String missingPlayerPosition) {
		
		List<Club> clubs = cr.findAll();
		
		if (name != null) {
			isValidName(name);
			clubs = filterByName(clubs, name);
		}
		if (minPlayers != null) {
			clubs = filterByMinPlayers(clubs, minPlayers);
		}
		if (maxPlayers != null) {
			clubs = filterByMaxPlayers(clubs, maxPlayers);
		}
		if (nWins != null) {
			clubs = filterByNWins(clubs, nWins);
		}
		if (nDraws != null) {
			clubs = filterByNDraws(clubs, nDraws);
		}
		if (nLosses != null) {
			clubs = filterByNLosses(clubs, nLosses);
		}
		if (nAchievements != null) {
			clubs = filterByNAchievements(clubs, nAchievements);
		}
		if (achievementPosition != null) {
			clubs = filterByAchievementPosition(clubs, achievementPosition);
		}
		if (missingPlayerPosition != null) {
			isValidPlayerPosition(missingPlayerPosition);
			clubs = filterByMissingPlayerPosition(clubs, missingPlayerPosition);
		}
		
		return clubs.stream().map(c -> ClubMapper.toFormatedDto(c)).collect(Collectors.toList());
	}
	
    /**
     * Validates that a name is not empty or null.
     *
     * @param name the name to validate
     * @return true if the name is valid
     * @throws InvalidClubDataException if name is invalid
     */
    private static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidClubDataException("Name can not be empty!");
        }
        return true;
    }
    
    /**
     * Validates that a position string represents a valid Position enum value.
     *
     * @param positionString the position string to validate
     * @return true if the position is valid
     * @throws InvalidClubDataException if position is invalid
     */
    private static boolean isValidPlayerPosition(String positionString) {
        try {
            Position.valueOf(positionString);
        } catch (IllegalArgumentException e) {
            throw new InvalidClubDataException("Player invalid position!");
        }
        return true;
    }

	/**
	 * Checks if a club has any future matches scheduled.
	 *
	 * @param club the club to check
	 * @return true if the club has future matches, otherwise false
	 * @throws InvalidClubDataException if the club has future matches
	 */
    private boolean checkIfClubHasFutureMatches(Club club) {
    	if (mr.upcomingMatchesForTeams(club.getTeamsIds(), LocalDate.now(), LocalTime.now())) {
			throw new InvalidClubDataException("Club has future matches to play!");
		}
    	return false;
    }

	/**
	 * Removes all teams from a club and updates the players accordingly.
	 *
	 * @param club the club from which to remove teams
	 */
    private void removeTeamsFromClub(Club club) {
    	for (Team t: club.getTeams()) {
    		if (t.getPlayers() != null) {
        		for (Player p: t.getPlayers()) {
        			p.getTeams().remove(t);
        			pr.save(p);
        		}
    		}
    		tr.delete(t);
    	}
    }

	/**
	 * Filters clubs by name.
	 *
	 * @param clubs the list of clubs to filter
	 * @param name the name to filter by
	 * @return a list of clubs that match the given name
	 */
    private List<Club> filterByName(List<Club> clubs, String name) {
    	return clubs.stream().filter(c -> c.getNameClub().equals(name)).collect(Collectors.toList());
    }

	/**
	 * Filters clubs by minimum number of players.
	 *
	 * @param clubs the list of clubs to filter
	 * @param minPlayers the minimum number of players required
	 * @return a list of clubs that have at least the specified number of players
	 */
    private List<Club> filterByMinPlayers(List<Club> clubs, Integer minPlayers) {
    	return clubs.stream().filter(c -> {
    		int totalPlayers = 0;
    		for (Team t: c.getTeams()) {
    			if (t.getPlayers() != null) {
        			totalPlayers += t.getPlayers().size();
    			}
    		}
    		return totalPlayers >= minPlayers;
    	})
    	.collect(Collectors.toList());
    }

	/**
	 * Filters clubs by maximum number of players.
	 * @param clubs the list of clubs to filter
	 * @param maxPlayers the maximum number of players allowed
	 * @return a list of clubs that have at most the specified number of players
	 */
    private List<Club> filterByMaxPlayers(List<Club> clubs, Integer maxPlayers) {
    	return clubs.stream().filter(c -> {
    		int totalPlayers = 0;
    		for (Team t: c.getTeams()) {
    			if (t.getPlayers() != null) {
        			totalPlayers += t.getPlayers().size();
    			}
    		}
    		return totalPlayers <= maxPlayers;
    	})
    	.collect(Collectors.toList());
    }

	/**
	 * Filters clubs by the number of wins.
	 * @param clubs the list of clubs to filter
	 * @param nWins the number of wins to filter by
	 * @return a list of clubs that have exactly the specified number of wins
	 */
    private List<Club> filterByNWins(List<Club> clubs, Integer nWins) {
    	return clubs.stream().filter(c -> {
    		return mr.countWinsByClub(c.getClubId()) == nWins;
    	})
    	.collect(Collectors.toList());
    }

	/**
	 * Filters clubs by the number of draws.
	 * @param clubs the list of clubs to filter
	 * @param nDraws the number of draws to filter by
	 * @return a list of clubs that have exactly the specified number of draws
	 */
    private List<Club> filterByNDraws(List<Club> clubs, Integer nDraws) {
    	return clubs.stream().filter(c -> {
    		return mr.countDrawsByClub(c.getClubId()) == nDraws;
    	})
    	.collect(Collectors.toList());
    }

	/**
	 * Filters clubs by the number of losses.
	 * @param clubs the list of clubs to filter
	 * @param nlosses the number of losses to filter by
	 * @return a list of clubs that have exactly the specified number of losses
	 */
    private List<Club> filterByNLosses(List<Club> clubs, Integer nlosses) {
    	return clubs.stream().filter(c -> {
    		return mr.countLossesByClub(c.getClubId()) == nlosses;
    	})
    	.collect(Collectors.toList());
    }

	/**
	 * Filters clubs by the number of achievements.
	 * @param clubs the list of clubs to filter
	 * @param nAchievements the number of achievements to filter by
	 * @return a list of clubs that have exactly the specified number of achievements
	 */
    private List<Club> filterByNAchievements(List<Club> clubs, Integer nAchievements) {
    	return clubs.stream().filter(c -> c.getAchievements().size() == nAchievements).collect(Collectors.toList());
    }

	/**
	 * Filters clubs by achievement position.
	 *
	 * @param clubs the list of clubs to filter
	 * @param achievementPosition the position in achievements to filter by
	 * @return a list of clubs that have an achievement with the specified position
	 */
    private List<Club> filterByAchievementPosition(List<Club> clubs, Integer achievementPosition) {
    	return clubs.stream().filter(c -> {
    		for (Achievement a: c.getAchievements()) {
    			if (a.getPlacement() == achievementPosition) {
    				return true;
    			}
    		}
    		return false;
    	})
    	.collect(Collectors.toList());
    }

	/**
	 * Filters clubs by missing player position.
	 *
	 * @param clubs the list of clubs to filter
	 * @param missingPlayerPosition the position that should not be present in any team of the club
	 * @return a list of clubs that do not have players in the specified position
	 */
    private List<Club> filterByMissingPlayerPosition(List<Club> clubs, String missingPlayerPosition) {
    	return clubs.stream().filter(c -> {
    		boolean val = true;
    		for (Team t: c.getTeams()) {
    			if (t.getPlayers() != null) {
    				for (Player p: t.getPlayers()) {
    					if (p.getPreferredPosition().equals(Position.valueOf(missingPlayerPosition))) {
    						val = false;
    					}
    				}
    			}
    		}
    		return val;
    	})
    	.collect(Collectors.toList());
    }
    
}
