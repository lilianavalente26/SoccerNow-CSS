package pt.ul.fc.css.soccernow.handlers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.exceptions.InvalidTeamDataException;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.mappers.TeamMapper;
import pt.ul.fc.css.soccernow.repositories.ClubRepository;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;
import pt.ul.fc.css.soccernow.repositories.RefereeRepository;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelTeam;

@Service
public class TeamHandler {

	private final TeamRepository tr;
	private final PlayerRepository pr;
	private final MatchRepository mr;
	private final ClubRepository cr;
	
    /**
     * Constructs a TeamService with the necessary repositories.
     *
     * @param tr TeamRepository for team operations
     * @param pr PlayerRepository for player operations
     * @param mr MatchRepository for match operations
     * @param cr ClubRepository for club operations
     */
	@Autowired
	public TeamHandler(TeamRepository tr, PlayerRepository pr, MatchRepository mr, ClubRepository cr) {
		this.tr = tr;
		this.pr = pr;
		this.mr = mr;
		this.cr = cr;
	}
	
    /**
     * Registers a new team with the provided data.
     *
     * @param teamData DTO containing team registration data
     * @return the ID of the newly registered team
     * @throws InvalidTeamDataException if the team data is invalid
     */
	@Transactional
	public long handleRegisterTeam(TeamDto teamData) {
		
		Team team = new Team();
		
		// Set Club
		if (cr.existsById(teamData.getClub())) {
			team.setClub(cr.findById(teamData.getClub()).get());
		} else {
			throw new InvalidTeamDataException("Club with Id " + teamData.getClub() + " does not exist!");
		}
		
		// Set Players
		if (teamData.getPlayers() != null) {
			team.setPlayers(new ArrayList<>());
			for (Long playerId: teamData.getPlayers()) {
				Optional<Player> optionalPlayer = pr.findById(playerId);
				if (optionalPlayer.isPresent()) {
					Player player = optionalPlayer.get();
					team.getPlayers().add(player);
				} else {
					throw new InvalidTeamDataException("Player with Id " + playerId + " does not exist!");
				}
			}
		} else {
			team.setPlayers(null);
		}
		
		// Set Goalkeeper
		if (pr.existsById(teamData.getGoalkeeper())) {
			if (pr.findById(teamData.getGoalkeeper()).get().getPreferredPosition().toString().equals("GOALKEEPER")) {
				team.setGoalkeeper(teamData.getGoalkeeper());
			} else {
				throw new InvalidTeamDataException("Goalkeeper with Id " + teamData.getGoalkeeper() + " does not have valid position!");
			}
		} else {
			throw new InvalidTeamDataException("Goalkeeper with Id " + teamData.getGoalkeeper() + " does not exist!");
		}
		
		// Set matchesAsTeam1
		if (teamData.getMatchesAsTeam1() != null) {
			team.setMatchesAsTeam1(new ArrayList<>());
			for (Long matchId: teamData.getMatchesAsTeam1()) {
				if (mr.existsById(matchId)) {
					team.getMatchesAsTeam1().add(mr.findById(matchId).get());
				} else {
					throw new InvalidTeamDataException("Match with Id " + matchId + " does not exist!");
				}
			}
		} else {
			team.setMatchesAsTeam1(null);
		}
		
		// Set matchesAsTeam2
		if (teamData.getMatchesAsTeam2() != null) {
			team.setMatchesAsTeam2(new ArrayList<>());
			for (Long matchId: teamData.getMatchesAsTeam2()) {
				if (mr.existsById(matchId)) {
					team.getMatchesAsTeam2().add(mr.findById(matchId).get());
				} else {
					throw new InvalidTeamDataException("Match with Id " + matchId + " does not exist!");
				}
			}
		} else {
			team.setMatchesAsTeam2(null);
		}
		
		tr.save(team);
		
		if (teamData.getPlayers() != null) {
			for (Long playerId : teamData.getPlayers()) {
				Player p = pr.findById(playerId).get();
			    p.addTeam(team);
			    pr.save(p);
			}
		}
		
		return team.getTeamId();
	}
	
    /**
     * Retrieves team data for the specified team ID.
     *
     * @param teamId the ID of the team to retrieve
     * @return TeamDto containing the team information
     * @throws InvalidTeamDataException if the team doesn't exist
     */
	public TeamDto handleGetTeam(Long teamId) {
		
		if (tr.existsById(teamId)) {
			
			Team team = tr.findById(teamId).get();
			TeamDto teamData = new TeamDto();
			
			teamData.setClub(team.getClub().getClubId());
			teamData.setPlayers(team.getPlayersIds());
			teamData.setGoalkeeper(team.getGoalkeeper());
			teamData.setMatchesAsTeam1(team.getMatchesAsTeam1Ids());
			teamData.setMatchesAsTeam2(team.getMatchesAsTeam2Ids());
			
			return teamData;
			
		} else {
			throw new InvalidTeamDataException("Team with Id " + teamId + " does not exist!");
		}
		
	}
	
    /**
     * Deletes a team with the specified ID after validating constraints.
     *
     * @param teamId the ID of the team to delete
     * @throws InvalidTeamDataException if the team doesn't exist or has future matches
     */
	@Transactional
	public void handleDeleteTeam(Long teamId) {
		
		if (tr.existsById(teamId)) {
			
			List<Long> teamIds = new ArrayList<>();
			teamIds.add(teamId);
			
			// Checks if team dont have future matches
			if (mr.upcomingMatchesForTeams(teamIds, LocalDate.now(), LocalTime.now())) {
				throw new InvalidTeamDataException("Can not remove team with future matches!");
			}
			
			// Remove team from players
			for (Long playerId: pr.findAllIds()) {
				if (pr.findById(playerId).get().getTeamsIds().contains(teamId)) {
					pr.findById(playerId).get().getTeams().remove(tr.findById(teamId).get());
				} 
			}			
			
			tr.deleteById(teamId);
			
		} else {
			throw new InvalidTeamDataException("Team with Id " + teamId + " does not exist!");
		}
		
	}
	
    /**
     * Updates a team with the specified ID using the provided data.
     * Validates all relationships and constraints before updating.
     *
     * @param teamId the ID of the team to update
     * @param teamData DTO containing the updated team data
     * @throws InvalidTeamDataException if the team doesn't exist or data is invalid
     * @throws InvalidUserDataException if player data is invalid
     */
	@Transactional
	public void handleUpdateTeam(Long teamId, TeamDto teamData) {
		
		Optional<Team> optionalTeam = tr.findByIdForUpdate(teamId);
		
		if (optionalTeam.isPresent()) {
			
			Team team = tr.findById(teamId).get();
			
			// Update Club
			if (cr.existsById(teamData.getClub())) {
				team.setClub(cr.findById(teamData.getClub()).get());
			} else {
				throw new InvalidTeamDataException("Club with Id " + teamData.getClub() + " does not exist!");
			}
			team.getClub().getTeams().remove(team);
			team.setClub(cr.findById(teamData.getClub()).get());
			team.getClub().addTeam(team);
			
			// Update Players
			for (Long playerId: team.getPlayersIds()) {
				pr.findById(playerId).get().getTeams().remove(team);
			}
			if (teamData.getPlayers() != null) {
				team.setPlayers(new ArrayList<>());
				for (Long playerId: teamData.getPlayers()) {
					Optional<Player> optionalPlayer = pr.findById(playerId);
					if (optionalPlayer.isPresent()) {
						Player player = optionalPlayer.get();
						team.getPlayers().add(player);
						player.getTeams().add(team);
						tr.save(team);
						pr.save(player);
					} else {
						throw new InvalidTeamDataException("Player with Id " + playerId + " does not exist!");
					}
				}
			} else {
				teamData.setPlayers(null);			
			}
			
			
			// Update Goalkeeper
			if (pr.existsById(teamData.getGoalkeeper())) {
				team.setGoalkeeper(teamData.getGoalkeeper());
			} else {
				throw new InvalidTeamDataException("Goalkeeper with Id " + teamData.getGoalkeeper() + " does not exist!");
			}
			
			
			// Update matchesAsTeam1
			if (team.getMatchesAsTeam1() == null && teamData.getMatchesAsTeam1() == null) {
				team.setMatchesAsTeam1(null);
			} else if (team.getMatchesAsTeam1() == null && teamData.getMatchesAsTeam1() != null) {
				team.setMatchesAsTeam1(new ArrayList<>());
				for (Long matchId: teamData.getMatchesAsTeam1()) {
					if (mr.existsById(matchId)) {
						team.getMatchesAsTeam1().add(mr.findById(matchId).get());
					} else {
						throw new InvalidTeamDataException("Match with Id " + matchId + " does not exist!");
					}
				}
			} else if (team.getMatchesAsTeam1() != null && teamData.getMatchesAsTeam1() == null) {
				List<Long> teamIds = new ArrayList<>();
				teamIds.add(teamId);
				
				// Checks if team dont have future matches
				if (mr.upcomingMatchesForTeams(teamIds, LocalDate.now(), LocalTime.now())) {
					throw new InvalidTeamDataException("Can not remove team with future matches!");
				}
				
				team.setMatchesAsTeam1(null);
			} else {
				List<Long> matchesToAdd = teamData.getMatchesAsTeam1();
				List<Long> matchesToRemove = new ArrayList<>();
				
				for (Long matchId: matchesToAdd) {
					if (!team.getMatchesAsTeam1Ids().contains(matchId)) {
						matchesToRemove.add(teamId);
					}
				}
				
				if (mr.hasUpcomingMatchesFromIds(matchesToRemove, LocalDate.now(), LocalTime.now())) {
					throw new InvalidTeamDataException("Team has future matches to play!");
				}
				
				team.setMatchesAsTeam1(new ArrayList<>());
				for (Long matchId: teamData.getMatchesAsTeam1()) {
					if (mr.existsById(matchId)) {
						team.getMatchesAsTeam1().add(mr.findById(matchId).get());
					} else {
						throw new InvalidTeamDataException("Match with Id " + matchId + " does not exist!");
					}
				}				
			}
			
			
			// Update matchesAsTeam2
			if (team.getMatchesAsTeam2() == null && teamData.getMatchesAsTeam2() == null) {
				team.setMatchesAsTeam2(null);
			} else if (team.getMatchesAsTeam2() == null && teamData.getMatchesAsTeam2() != null) {
				team.setMatchesAsTeam2(new ArrayList<>());
				for (Long matchId: teamData.getMatchesAsTeam2()) {
					if (mr.existsById(matchId)) {
						team.getMatchesAsTeam2().add(mr.findById(matchId).get());
					} else {
						throw new InvalidTeamDataException("Match with Id " + matchId + " does not exist!");
					}
				}
			} else if (team.getMatchesAsTeam2() != null && teamData.getMatchesAsTeam2() == null) {
				List<Long> teamIds = new ArrayList<>();
				teamIds.add(teamId);
				
				// Checks if team dont have future matches
				if (mr.upcomingMatchesForTeams(teamIds, LocalDate.now(), LocalTime.now())) {
					throw new InvalidTeamDataException("Can not remove team with future matches!");
				}
				
				team.setMatchesAsTeam2(null);
			} else {
				List<Long> matchesToAdd = teamData.getMatchesAsTeam2();
				List<Long> matchesToRemove = new ArrayList<>();
				
				for (Long matchId: matchesToAdd) {
					if (!team.getMatchesAsTeam2Ids().contains(matchId)) {
						matchesToRemove.add(teamId);
					}
				}
				
				if (mr.hasUpcomingMatchesFromIds(matchesToRemove, LocalDate.now(), LocalTime.now())) {
					throw new InvalidTeamDataException("Team has future matches to play!");
				}
				
				team.setMatchesAsTeam2(new ArrayList<>());
				for (Long matchId: teamData.getMatchesAsTeam2()) {
					if (mr.existsById(matchId)) {
						team.getMatchesAsTeam2().add(mr.findById(matchId).get());
					} else {
						throw new InvalidTeamDataException("Match with Id " + matchId + " does not exist!");
					}
				}				
			}
			
			tr.save(team);
									
		} else {
			throw new InvalidTeamDataException("Team with Id " + teamId + " does not exist!");
		}
	}
	
	/**
	 * Handles the search of teams based on parameters
	 * 
	 * @param teamDto information about team for search
	 * @return the list of all teams that corresponds to search options in dto
	 */
	public String handleSeatchTeams(TeamDto teamDto) {
		List<Long> teams = tr.findAllIds();
	
		if (teamDto.getClub() != null) {
			teams = teams.stream().filter(t -> tr.findById(t).get().getClub().getClubId() == teamDto.getClub())
					.collect(Collectors.toList());
		}
	
		if (teamDto.getGoalkeeper() != null) {
			teams = teams.stream().filter(t -> tr.findById(t).get().getGoalkeeper() == teamDto.getGoalkeeper())
					.collect(Collectors.toList());
		}
	
		if (teamDto.getPlayers() != null) {
			teams = teams.stream().filter(t -> {
				List<Long> currentPlayersIds = tr.findById(t).get().getPlayersIds();
				for (Long playerId: currentPlayersIds) {
					if (!teamDto.getPlayers().contains(playerId)) {
						return false;
					}
				}
				return true;
			})
			.collect(Collectors.toList());
		}
	
		if (teamDto.getMatchesAsTeam1() != null) {
			teams = teams.stream().filter(t -> {
				List<Long> currentMatchesIds = tr.findById(t).get().getMatchesAsTeam1Ids();
				for (Long matchId: currentMatchesIds) {
					if (!teamDto.getMatchesAsTeam1().contains(matchId)) {
						return false;
					}
				}
				return true;
			})
			.collect(Collectors.toList());
		}
	
		if (teamDto.getMatchesAsTeam2() != null) {
			teams = teams.stream().filter(t -> {
				List<Long> currentMatchesIds = tr.findById(t).get().getMatchesAsTeam2Ids();
				for (Long matchId: currentMatchesIds) {
					if (!teamDto.getMatchesAsTeam2().contains(matchId)) {
						return false;
					}
				}
				return true;
			})
			.collect(Collectors.toList());
		}
	
		return teams.toString();
	}
	
	/**
	 * Retrieves all teams from the clubId
	 * 
	 * @param clubId the id of the club
	 * @return all teams in formatedDto
	 */
	public List<ViewModelTeam> handleGetAllTeamsOfClub(Long clubId) {
		List<Team> teams = tr.findAllByClubClubId(clubId);
		return teams.stream().map(t -> TeamMapper.toViewModel(t, pr.findById(t.getGoalkeeper()).get().getName())).collect(Collectors.toList());
	}
}