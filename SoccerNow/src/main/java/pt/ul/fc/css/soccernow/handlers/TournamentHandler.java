package pt.ul.fc.css.soccernow.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.entities.Achievement;
import pt.ul.fc.css.soccernow.entities.Club;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.PointsTournament;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.entities.TournamentStanding;
import pt.ul.fc.css.soccernow.exceptions.InvalidTournamentDataException;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.mappers.PointsTournamentMapper;
import pt.ul.fc.css.soccernow.repositories.AchievementRepository;
import pt.ul.fc.css.soccernow.repositories.ClubRepository;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.PointsTournamentRepository;
import pt.ul.fc.css.soccernow.repositories.TournamentStandingRepository;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPointsTournament;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentHandler {

    private final PointsTournamentRepository ptr;
    private final ClubRepository cr;
    private final AchievementRepository ar;
    private final MatchRepository mr;
    private final TournamentStandingRepository tsr;

    @Autowired
    public TournamentHandler(PointsTournamentRepository ptr, ClubRepository cr, AchievementRepository ar, MatchRepository mr, TournamentStandingRepository tsr) {
        this.cr = cr;
        this.ar = ar;
        this.mr = mr;
        this.ptr = ptr;
        this.tsr = tsr;
    }

	/**
	 * Registers a new tournament with the given details.
	 *
	 * @param tDto the TournamentDto containing tournament details
	 * @return the ID of the newly registered tournament
	 * @throws InvalidTournamentDataException if the tournament name is invalid or clubs/matches are not valid
	 */
    @Transactional
    public long handleRegisterTournament(TournamentDto tDto) {
        isValidName(tDto.getTournamentName());
        List<Club> validClubs = getValidClubs(tDto.getClubs());
        List<Match> validMatches = getValidMatches(tDto.getMatches());
     
        PointsTournament tournament = PointsTournamentMapper.toPointsTournament(tDto, validClubs, validMatches, null);
        tournament = ptr.save(tournament);
        
        tournament.setStandings(getValidStandings(tournament, validClubs));
        ptr.save(tournament); 
        return tournament.getTournamentId();
    }

	/**
	 * Retrieves a tournament by its ID.
	 *
	 * @param id the ID of the tournament
	 * @return the TournamentDto of the requested tournament
	 * @throws InvalidTournamentDataException if the tournament does not exist
	 */
    public TournamentDto handleGetTournament(Long id) {
    	Optional<PointsTournament> optionalTournament = ptr.findById(id);
    	if (optionalTournament.isPresent()) {
    		return PointsTournamentMapper.toDto(optionalTournament.get());
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Retrieves the standings of a tournament by its ID.
	 *
	 * @param id the ID of the tournament
	 * @return a string representation of the tournament standings
	 * @throws InvalidTournamentDataException if the tournament does not exist or is not a points tournament
	 */
    public String handleGetTournamentStandings(Long id) {
    	Optional<PointsTournament> optionalTournament = ptr.findById(id);
    	if (optionalTournament.isPresent()) {
    		if (optionalTournament.get() instanceof PointsTournament) {
        		PointsTournament tournament = optionalTournament.get();
        		List<TournamentStanding> standings = tournament.getStandings();
        		StringBuilder sb = new StringBuilder();
        		Map<Club, Integer> clubPlacements = getClubPlacements(tournament);
        		
        		for (int i = 1; i <= tournament.getClubs().size(); i++) {
        			for (Club c: clubPlacements.keySet()) {
        				if (clubPlacements.get(c) == i) {
        					sb.append("Club: " + c.getNameClub() + " | ");
        					sb.append("Position: " + i + " | ");
        					for (TournamentStanding standing: standings) {
        						if (standing.getClub().getClubId() == c.getClubId()) {
        		        			sb.append("Points: " + standing.getPoints() + " | ");
        		        			sb.append("Goals Scored: " + standing.getGoalsScored() + " | ");
        		        			sb.append("Goals Conceded: " + standing.getGoalsConceded() + "\n");
        						}
        					}
        				}
        			}
        		}
        		
        		return sb.toString();
    		} else {
    			throw new InvalidTournamentDataException("This is not a points tournament!");    
    		}    		
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Deletes a tournament by its ID.
	 *
	 * @param id the ID of the tournament to delete
	 * @throws InvalidTournamentDataException if the tournament does not exist or has matches
	 */
    @Transactional
    public void handleDeleteTournament(Long id) {
    	Optional<PointsTournament> optionalTournament = ptr.findById(id);
    	if (optionalTournament.isPresent()) {
    		PointsTournament pt = optionalTournament.get();
    		if (pt.getMatches().size() == 0) {
    			for (Club club: pt.getClubs()) {
    				club.getTournaments().remove(pt);
    			}
    			ptr.delete(pt);
    		} else {
    			throw new InvalidTournamentDataException("Can not remove a tournament with matches!");
    		}
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Updates the name of a tournament.
	 * @param id the ID of the tournament to update
	 * @param newName the new name for the tournament
	 */
	@Transactional
    public void handleUpdateTournamentName(Long id, String newName) {
    	Optional<PointsTournament> optionalTournament = ptr.findByIdForUpdate(id);
    	if (optionalTournament.isPresent()) {
    		PointsTournament pt = optionalTournament.get();
    		isValidName(newName);
    		pt.setTournamentName(newName);
    		ptr.save(pt);
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Updates the date and time of a tournament.
	 * @param id the ID of the tournament to update
	 */
	@Transactional
    public void handleUpdateTournamentOver(Long id) {
    	Optional<PointsTournament> optionalTournament = ptr.findByIdForUpdate(id);
    	if (optionalTournament.isPresent()) {
    		PointsTournament pt = optionalTournament.get();
    		if (pt.isOver()) {
    			throw new InvalidTournamentDataException("Tournament is already over!");  
    		}
    		for (Match m: pt.getMatches()) {
    			if (!m.getStats().isOver()) {
    				throw new InvalidTournamentDataException("Can not end a tournament with future matches to play!");  
    			}
    		}
    		updateClubAchievements(pt);
    		pt.setOver(true);
    		ptr.save(pt);
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Handles the update of clubs in a tournament.
	 * @param id the ID of the tournament to update
	 * @param clubToAdd the ID of the club to add to the tournament, or null if no club is to be added
	 * @param clubToRemove the ID of the club to remove from the tournament, or null if no club is to be removed
	 */
    @Transactional
    public void handleUpdateTournamentClubs(long id, Long clubToAdd, Long clubToRemove) {
    	Optional<PointsTournament> optionalTournament = ptr.findByIdForUpdate(id);
    	if (optionalTournament.isPresent()) {
    		PointsTournament pt = optionalTournament.get();
    		if (clubToAdd != null) {
        		addClubTournament(pt, clubToAdd);	
    		}
    		if (clubToRemove != null) {
        		if (pt.getClubs().size() <= 8) {
        			throw new InvalidTournamentDataException("Tournament requires a minimum amount of 8 clubs!");
        		}

        		removeClubTournament(pt, clubToRemove);
    		}
    		ptr.save(pt);
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Handles the update of matches in a tournament.
	 * @param id the ID of the tournament to update
	 * @param matchId the ID of the match to add to the tournament, or null if no match is to be added
	 */
	@Transactional
    public void handleUpdateTournamentCancelMatch(Long id, Long matchId) {
    	Optional<PointsTournament> optionalTournament = ptr.findByIdForUpdate(id);
    	if (optionalTournament.isPresent()) {
			PointsTournament t = optionalTournament.get();
    		Optional<Match> optionalMatch = mr.findById(matchId);
    		if (optionalMatch.isPresent()) {
    			if (t.getMatches().contains(optionalMatch.get())) {
    				t.getMatches().remove(optionalMatch.get());
    				mr.delete(optionalMatch.get());
    				ptr.save(t);
    			} else {
    				throw new InvalidTournamentDataException("Can not cancel a match that in not in the tournament!");  
    			}
    		} else {
    			throw new InvalidTournamentDataException("Match with Id " + matchId + " does not exist!");    
    		}
    	} else {
    		throw new InvalidTournamentDataException("Tournament with Id " + id + " does not exist!");    		
    	}
    }

	/**
	 * Filters tournaments by if the contain a given string in the name.
	 * @param name the string to search for in tournament names
	 * @return a list of TournamentDto objects that match the search criteria
	 */
	public List<TournamentDto> handleSearchTournamentByName(String name) {
        List<TournamentDto> tournaments = new ArrayList<>();
        List<PointsTournament> tournamentsList = ptr.findByTournamentNameContainingIgnoreCase(name);
        for (PointsTournament tournament : tournamentsList) {
            tournaments.add(PointsTournamentMapper.toDto(tournament));
        }
        return tournaments;
    }

	/**
	 * Filters tournaments by their status (over or not).
	 * @param isOver true if searching for tournaments that are over, false otherwise
	 * @return a list of TournamentDto objects that match the search criteria
	 */
    public List<TournamentDto> handleSearchTournamentByStatus(Boolean isOver) {
        List<TournamentDto> tournaments = new ArrayList<>();
        List<PointsTournament> tournamentsList = ptr.findByIsOver(isOver);
        for (PointsTournament tournament : tournamentsList) {
            tournaments.add(PointsTournamentMapper.toDto(tournament));
        }
        return tournaments;
    }
    
    /**
     * Validates that a name is not empty or null.
     *
     * @param name the name to validate
     * @return true if the name is valid
     * @throws InvalidTournamentDataException if name is invalid
     */
    private static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidTournamentDataException("Name can not be empty!");
        }
        return true;
    }

	/**
	 * Validates that a list of clubs is valid for a tournament.
	 *
	 * @param clubs the list of club IDs to validate
	 * @return a list of valid Club entities
	 * @throws InvalidTournamentDataException if the clubs are invalid
	 */
    private List<Club> getValidClubs(List<Long> clubs) {
    	List<Club> validClubs = new ArrayList<>();
    	if (!(clubs.stream().distinct().count() == clubs.size())) {
    		throw new InvalidTournamentDataException("Tournament can not have repeated clubs!");
    	}
    	if (clubs != null && !clubs.isEmpty() && !(clubs.size() == 1 || clubs.get(0) == 0)) {
    		for (Long clubId: clubs) {
    			Optional<Club> optionalClub = cr.findById(clubId);
    			if (optionalClub.isPresent()) {
    				validClubs.add(optionalClub.get());
    			} else {
    				throw new InvalidTournamentDataException("Club with Id " + clubId + " does not exist!");
    			}
    		}
    	}
    	if (validClubs.size() < 8) {
    		throw new InvalidTournamentDataException("Tournament requires a minimum amount of 8 clubs!");
    	}
    	return validClubs;
    }

	/**
	 * Validates that a list of matches is valid for a tournament.
	 *
	 * @param matches the list of match IDs to validate
	 * @return a list of valid Match entities
	 * @throws InvalidTournamentDataException if the matches are invalid
	 */
    private List<Match> getValidMatches(List<Long> matches) {
    	List<Match> validMatches = new ArrayList<>();
    	if (matches != null && !matches.isEmpty() && !(matches.size() == 1 || matches.get(0) == 0)) {
    		for (Long matchId: matches) {
    			Optional<Match> optionalMatch = mr.findById(matchId);
    			if (optionalMatch.isPresent()) {
    				validMatches.add(optionalMatch.get());
    			} else {
    				throw new InvalidTournamentDataException("Match with Id " + matchId + " does not exist!");
    			}
    		}
    	}
    	return validMatches;
    }

	/**
	 * Creates a list of valid TournamentStanding objects for the given tournament and clubs.
	 *
	 * @param t the tournament for which to create standings
	 * @param validClubs the list of valid clubs participating in the tournament
	 * @return a list of TournamentStanding objects initialized with zero points and goals
	 */
    private List<TournamentStanding> getValidStandings(Tournament t, List<Club> validClubs) {
    	
    	List<TournamentStanding> validStandings = new ArrayList<>();
    	for (Club c: validClubs) {
    		TournamentStanding ts = new TournamentStanding();
            ts.setTournament(t);
            ts.setClub(c);
    		ts.setPoints(0);
    		ts.setGoalsScored(0);
    		ts.setGoalsConceded(0);
    		validStandings.add(ts);
    	}
    	return validStandings;
    }

	/**
	 * Updates achievements for clubs in a PointsTournament based on their placements.
	 *
	 * @param pt the PointsTournament to update achievements for
	 */
    private void updateClubAchievements(PointsTournament pt) {
    	Map<Club, Integer> clubPlacements = getClubPlacements(pt);
    	for (Club c: pt.getClubs()) {
    		if (clubPlacements.get(c) <= 3) {
    			Achievement a = new Achievement();
        		a.setClub(c);
        		a.setTournament(pt);
        		a.setPlacement(clubPlacements.get(c));
        		ar.save(a);
        		c.getAchievements().add(a);
        		cr.save(c);
    		}
    	}
    }

	/**
	 * Calculates the placements of clubs in a PointsTournament based on their points.
	 *
	 * @param pt the PointsTournament to calculate placements for
	 * @return a map of clubs to their placements
	 */
    private Map<Club, Integer> getClubPlacements(PointsTournament pt) {
    	Map<Club, Integer> clubPlacements = new HashMap<>();
    	List<Club> remainingClubs = new ArrayList<>(pt.getClubs());
		int position = 1;
    	while (clubPlacements.size() <  pt.getClubs().size()) {
    		List<Club> clubsTested = new ArrayList<>();
    		int currentMaxPoints = 0;
    		for (Club c: remainingClubs) {
    			if (pt.getClubPoints(c) > currentMaxPoints) {
    				currentMaxPoints = pt.getClubPoints(c);
    				clubsTested = new ArrayList<>();
    				clubsTested.add(c);
    			} else if (pt.getClubPoints(c) == currentMaxPoints) {
    				clubsTested.add(c);
    			}
        	}
    		remainingClubs.removeAll(clubsTested);
    		for (Club c: clubsTested) {
    			clubPlacements.put(c, position);
    		}
    		clubsTested = new ArrayList<>();
    		position += 1;
    	}
    	return clubPlacements;
    }

	/**
	 * Adds a club to a tournament if it is not already present.
	 *
	 * @param t the tournament to which the club will be added
	 * @param newClubId the ID of the club to add
	 * @throws InvalidTournamentDataException if the club does not exist or is already in the tournament
	 */
    private void addClubTournament(Tournament t, Long newClubId) {
    	Optional<Club> optionalClub = cr.findById(newClubId);
    	if (optionalClub.isPresent()) {
    		if (!t.getClubs().contains(optionalClub.get())) {
        		t.addClub(optionalClub.get());
        		optionalClub.get().addTournament(t);
    		} else {
    			throw new InvalidTournamentDataException("Can not add a club that already is in the tournament!");
    		}
    	} else {
    		throw new InvalidTournamentDataException("Club with Id " + newClubId + " does not exist!");
    	}
    }

	/**
	 * Removes a club from a tournament if it is present and does not have matches.
	 *
	 * @param t the tournament from which the club will be removed
	 * @param removeClubId the ID of the club to remove
	 * @throws InvalidTournamentDataException if the club does not exist, is not in the tournament, or has matches
	 */
    private void removeClubTournament(Tournament t, Long removeClubId) {
    	Optional<Club> optionalClub = cr.findById(removeClubId);
    	if (optionalClub.isPresent()) {
    		canRemoveCLubFromTournament(t, optionalClub.get());
    		t.getClubs().remove(optionalClub.get());
    		optionalClub.get().getTournaments().remove(t);
    	} else {
    		throw new InvalidTournamentDataException("Club with Id " + removeClubId + " does not exist!");
    	}
    }

	/**
	 * Checks if a club can be removed from a tournament.
	 *
	 * @param t the tournament from which the club will be removed
	 * @param club the club to check
	 * @return true if the club can be removed, false otherwise
	 * @throws InvalidTournamentDataException if the club is not in the tournament or has matches
	 */
    private boolean canRemoveCLubFromTournament(Tournament t, Club club) {
    	if (!t.getClubs().contains(club)) {
    		throw new InvalidTournamentDataException("Can not remove a club that is not in the tournament!");
    	}
    	for (Match match: t.getMatches()) {
    		if (match.getTeam1().getClub().getClubId() == club.getClubId() || match.getTeam2().getClub().getClubId() == club.getClubId()) {
    			throw new InvalidTournamentDataException("Can not remove a club that already has matches in the tournament!");
    		}
    	}
    	return true;
    }

	/**
	 * Searches for tournaments associated with a specific club.
	 * @param clubId the ID of the club to search for tournaments
	 * @return a list of TournamentDto objects associated with the club
	 */
    public List<TournamentDto> handleSearchTournamentByClub(Long clubId) {
		List<TournamentDto> tournaments = new ArrayList<>();
		Optional<Club> optionalClub = cr.findById(clubId);
		if (optionalClub.isPresent()) {
			Club club = optionalClub.get();
			List<Tournament> clubTournaments = club.getTournaments();
			if (clubTournaments != null && !clubTournaments.isEmpty()) {
				for (Tournament tournament : clubTournaments) {
					tournaments.add(PointsTournamentMapper.toDto((PointsTournament) tournament));
				}
			} else {
				throw new InvalidTournamentDataException("Club with Id " + clubId + " has no tournaments!");
			}
		} else {
			throw new InvalidTournamentDataException("Club with Id " + clubId + " does not exist!");
		}
		return tournaments;
    }
    
    public List<ViewModelPointsTournament> handleGetAllPointsTournaments() {
    	List<PointsTournament> tournaments = ptr.findAll();
    	return tournaments.stream().map(t -> {
			Map<Club, Integer> standings = getClubPlacements(t);
			return PointsTournamentMapper.toViewModel(t, standings);
		})
		.collect(Collectors.toList());
    }
    
	public List<ViewModelPointsTournament> filterByTournament(String name, String clubName, Integer minRealizedMatches, Integer maxRealizedMatches, Integer minToDoMatches, Integer maxToDoMatches) {
		List<PointsTournament> tournaments = ptr.findAll();
		
		if (name != null) {
			tournaments = filterByName(tournaments, name);
		}
		if (clubName != null) {
			tournaments = filterByClub(tournaments, clubName);
		}
		if (minRealizedMatches != null || maxRealizedMatches != null) {
			tournaments.retainAll(filterByMatchesPlayed(true, minRealizedMatches, maxRealizedMatches));
		}
		if (minToDoMatches != null || maxToDoMatches != null) {
			tournaments.retainAll(filterByMatchesPlayed(false, minToDoMatches, maxToDoMatches));
		}
		
		return tournaments.stream().map(t -> {
			Map<Club, Integer> standings = getClubPlacements(t);
			return PointsTournamentMapper.toViewModel(t, standings);
		})
		.collect(Collectors.toList());
	}
	
	private List<PointsTournament> filterByName(List<PointsTournament> pts, String name) {
		return pts.stream().filter(t -> t.getTournamentName().equals(name)).collect(Collectors.toList());
	}
	
	private List<PointsTournament> filterByClub(List<PointsTournament> pts, String club) {
		return pts.stream().filter(t -> {
			for (Club c: t.getClubs()) {
				if (c.getNameClub().equals(club)) {
					return true;
				}
			}
			return false;
		})
		.collect(Collectors.toList());
	}

	public List<PointsTournament> filterByMatchesPlayed(boolean alreadyPlayed, Integer minMatches, Integer maxMatches) {
		if ((maxMatches != null && maxMatches < 0) || (minMatches != null && minMatches < 0)) {
			throw new InvalidUserDataException("Matches played cannot be negative!");
		}
		if (minMatches != null && maxMatches != null && minMatches > maxMatches) {
			throw new InvalidUserDataException("Minimum matches cannot be greater than maximum matches!");
		}
		List<Match> matches = mr.findAll();
		// Mapa para contar o número de jogos que já foram jogados por torneio
		Map<Long, Integer> tournamentMatchCountPlayed = new HashMap<>();

		// Mapa para contar o número de jogos que serão jogados por torneio
		Map<Long, Integer> tournamentMatchCountToPlay = new HashMap<>();

		// Para cada jogo
		for (Match match : matches) {
			// Verifica que tem torneio associado
			if (match.getTournament() != null) {
				// Vê se o jogo já foi jogado (aka está terminado)
				if (match.getStats().isOver()) {
					// Adiciona o match ao contador de matches já jogados do torneio
					Long tournamentId = match.getTournamentId();
					tournamentMatchCountPlayed.put(tournamentId, tournamentMatchCountPlayed.getOrDefault(tournamentId, 0) + 1);
				} else {
					// Adiciona o match ao contador de matches que ainda vão ser jogados do torneio
					Long tournamentId = match.getTournamentId();
					tournamentMatchCountToPlay.put(tournamentId, tournamentMatchCountToPlay.getOrDefault(tournamentId, 0) + 1);
				}
			}
		}

		// cria a lista vazia para por os torneios filtrados
		List<PointsTournament> tournaments = new ArrayList<>();
		if (alreadyPlayed) {
			for (Map.Entry<Long, Integer> entry : tournamentMatchCountPlayed.entrySet()) {
				int count = entry.getValue();

				// Aplicar os filtros
				if ((maxMatches != null && count > maxMatches) ||
						(minMatches != null && count < minMatches)) {
					// Se não passou nos filtros, continua para o próximo torneio, sem adicionar
					continue;
				}
				// Se passou nos filtros, adiciona o torneio à lista
				Optional<PointsTournament> optionalTournament = ptr.findById(entry.getKey());
				if (optionalTournament.isPresent()) {
					tournaments.add(optionalTournament.get());
				} else {
					// Só porque ainda não tinhamos feito, acho que não faz mal
					throw new InvalidTournamentDataException("Tournament with Id " + entry.getKey() + " does not exist!");
				}
			}
		} else {
			for (Map.Entry<Long, Integer> entry : tournamentMatchCountToPlay.entrySet()) {
				int count = entry.getValue();

				// Aplicar os filtros
				if ((maxMatches != null && count > maxMatches) ||
						(minMatches != null && count < minMatches)) {
					// Se não passou nos filtros, continua para o próximo torneio, sem adicionar
					continue;
				}
				// Se passou nos filtros, adiciona o torneio à lista
				Optional<PointsTournament> optionalTournament = ptr.findById(entry.getKey());
				if (optionalTournament.isPresent()) {
					tournaments.add(optionalTournament.get());
				} else {
					// Só porque ainda não tinhamos feito, acho que não faz mal
					throw new InvalidTournamentDataException("Tournament with Id " + entry.getKey() + " does not exist!");
				}
			}
		}
		return tournaments;
	}
	
}
