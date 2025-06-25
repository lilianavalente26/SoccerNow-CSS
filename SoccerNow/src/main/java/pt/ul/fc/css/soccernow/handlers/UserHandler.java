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

import pt.ul.fc.css.soccernow.dto.*;
import pt.ul.fc.css.soccernow.entities.*;
import pt.ul.fc.css.soccernow.enums.Position;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.mappers.PlayerMapper;
import pt.ul.fc.css.soccernow.mappers.RefereeMapper;
import pt.ul.fc.css.soccernow.repositories.*;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPlayer;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelReferee;


@Service
public class UserHandler {
    
    private final PlayerRepository pr;
    private final RefereeRepository rr;
    private final MatchRepository mr;
    private final TeamRepository tr;
    private final GoalRepository gr;
    private final CardRepository cr;
    
    /**
     * Constructs a UserHandler with the necessary repositories.
     *
     * @param pr PlayerRepository for player operations
     * @param rr RefereeRepository for referee operations
     * @param cr CardRepository for club operations
     * @param mr MatchRepository for match operations
     * @param tr TeamRepository for team operations
     */
    @Autowired
    public UserHandler(PlayerRepository pr,
                       RefereeRepository rr,
                       MatchRepository mr,
                       TeamRepository tr,
                       GoalRepository gr,
                       CardRepository cr) {
        this.pr = pr;
        this.rr = rr;
        this.mr = mr;
        this.tr = tr;
        this.gr = gr;
        this.cr = cr;
    }
    
    /**
     * Registers a new player with the provided data.
     *
     * @param userData DTO containing player registration data
     * @return the ID of the newly registered player
     * @throws InvalidUserDataException if the player data is invalid
     */
    @Transactional
    public long handleRegisterPlayer(PlayerDto userData) throws InvalidUserDataException {
        isValidName(userData.getName());
        isValidPlayerPosition(userData.getPreferedPosition());        
        Player player = PlayerMapper.toPlayer(userData, getValidPlayerTeams(userData.getTeams()));
        pr.save(player);
        return player.getUserId();
    }
    
    /**
     * Retrieves player data for the specified player ID.
     *
     * @param playerId the ID of the player to retrieve
     * @return PlayerDto containing the player information
     * @throws InvalidUserDataException if the player doesn't exist
     */
    public PlayerDto handleGetPlayer(Long playerId) {
        Optional<Player> optionalPlayer = pr.findById(playerId);    
        if (optionalPlayer.isPresent()) {
            return PlayerMapper.toDto(optionalPlayer.get());
        } else {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
    }
    
    /**
     * Deletes a player with the specified ID.
     *
     * @param playerId the ID of the player to delete
     * @throws InvalidUserDataException if the player doesn't exist or has future matches
     */
    @Transactional
    public void handleDeletePlayer(Long playerId) {    
        if (!pr.existsById(playerId)) {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
        playerHasNotFutureMatches(playerId);
        removePlayerFromTeams(playerId);
        pr.deleteById(playerId);        
    }
    
    /**
     * Updates a player's name.
     *
     * @param playerId the ID of the player to update
     * @param newName the new name for the player
     * @throws InvalidUserDataException if the player doesn't exist or name is invalid
     */
    @Transactional
    public void handleUpdatePlayerName(Long playerId, String newName) {
        Optional<Player> optionalPlayer = pr.findByIdForUpdate(playerId);    
        if (optionalPlayer.isPresent()) {
            isValidName(newName);
            Player updatedPlayer = optionalPlayer.get();
            updatedPlayer.setName(newName);
            pr.save(updatedPlayer);
        } else {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
    }
    
    /**
     * Updates a player's preferred position.
     *
     * @param playerId the ID of the player to update
     * @param newPosition the new position for the player
     * @throws InvalidUserDataException if the player doesn't exist or position is invalid
     */
    @Transactional
    public void handleUpdatePlayerPosition(Long playerId, String newPosition) {
        Optional<Player> optionalPlayer = pr.findByIdForUpdate(playerId);    
        if (optionalPlayer.isPresent()) {
            isValidPlayerPosition(newPosition);
            Player updatedPlayer = optionalPlayer.get();
            updatedPlayer.setPreferredPosition(Position.valueOf(newPosition));
            pr.save(updatedPlayer);
        } else {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
    }
    
    /**
     * Adds a team to a player's team list.
     *
     * @param playerId the ID of the player to update
     * @param teamId the ID of the team to add
     * @throws InvalidUserDataException if player or team doesn't exist, or player is already in the team
     */
    @Transactional
    public void handleUpdateAddPlayerTeam(Long playerId, Long teamId) {
        Optional<Player> optionalPlayer = pr.findByIdForUpdate(playerId);    
        if (optionalPlayer.isPresent()) {
            Team newTeam = tr.findById(teamId).orElseThrow(() -> new InvalidUserDataException("Team with Id " + teamId + " does not exist!"));
            Player updatedPlayer = optionalPlayer.get();
            if (updatedPlayer.getTeams() != null && updatedPlayer.getTeams().contains(newTeam)) {
                throw new InvalidUserDataException("Player already in the team!");
            }
            updatedPlayer.addTeam(newTeam);
            pr.save(updatedPlayer);
        } else {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
    }
    
    /**
     * Removes a team from a player's team list.
     *
     * @param playerId the ID of the player to update
     * @param teamId the ID of the team to remove
     * @throws InvalidUserDataException if player or team doesn't exist, or player isn't in the team
     */
    @Transactional
    public void handleUpdateRemovePlayerTeam(Long playerId, Long teamId) {
        Optional<Player> optionalPlayer = pr.findByIdForUpdate(playerId);    
        if (optionalPlayer.isPresent()) {
            Player updatedPlayer = optionalPlayer.get();
            Team toRemoveTeam = tr.findById(teamId).orElseThrow(() -> new InvalidUserDataException("Team with Id " + teamId + " does not exist!"));
            removeTeamFromPlayer(updatedPlayer, toRemoveTeam);
            pr.save(updatedPlayer);
        } else {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
    }
    
    /**
     * Updates a player with the specified ID using the provided data.
     *
     * @param playerId the ID of the player to update
     * @param userData DTO containing the updated player data
     * @throws InvalidUserDataException if the player doesn't exist or data is invalid
     */
    @Transactional
    public void handleUpdatePlayer(Long playerId, PlayerDto userData) {
        Optional<Player> optionalPlayer = pr.findByIdForUpdate(playerId);    
        if (optionalPlayer.isPresent()) {
            Player updatedPlayer = optionalPlayer.get();
            handleUpdatePlayerName(playerId, userData.getName());
            handleUpdatePlayerPosition(playerId, userData.getPreferedPosition());
            if (userData.getTeams() == null) {
                userData.setTeams(new ArrayList<>());
            }
            updateTeamsForPlayer(updatedPlayer, userData.getTeams(), updatedPlayer.getTeamsIds());
            pr.save(updatedPlayer);
        } else {
            throw new InvalidUserDataException("Player with Id " + playerId + " does not exists!");
        }
    }
       
    /**
     * Registers a new referee with the provided data.
     *
     * @param userData DTO containing referee registration data
     * @return the ID of the newly registered referee
     * @throws InvalidUserDataException if the referee data is invalid
     */
    @Transactional
    public long handleRegisterReferee(RefereeDto userData) {
        isValidName(userData.getName());    
        Referee referee = RefereeMapper.toReferee(userData, getValidRefereeMatches(userData.getMatches()));
        rr.save(referee);
        for (Match m: referee.getMatches()) {
            m.getReferees().add(referee);
            mr.save(m);
        }
        return referee.getUserId();
    }
    
    /**
     * Retrieves referee data for the specified referee ID.
     *
     * @param refereeId the ID of the referee to retrieve
     * @return RefereeDto containing the referee information
     * @throws InvalidUserDataException if the referee doesn't exist
     */
    public RefereeDto handleGetReferee(Long refereeId) {
        Optional<Referee> optionalReferee = rr.findById(refereeId);    
        if (optionalReferee.isPresent()) {
            return RefereeMapper.toDto(optionalReferee.get());
        } else {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
    }
    
    /**
     * Deletes a referee with the specified ID.
     *
     * @param refereeId the ID of the referee to delete
     * @throws InvalidUserDataException if the referee doesn't exist or has future matches
     */
    @Transactional
    public void handleDeleteReferee(Long refereeId) {
        if (!rr.existsById(refereeId)) {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
        refereeHasNotFutureMatches(refereeId);
        removeRefereeFromMatches(refereeId);
        rr.deleteById(refereeId);
    }
    
    /**
     * Updates a referee's name.
     *
     * @param refereeId the ID of the referee to update
     * @param newName the new name for the referee
     * @throws InvalidUserDataException if the referee doesn't exist or name is invalid
     */
    @Transactional
    public void handleUpdateRefereeName(Long refereeId, String newName) {
        Optional<Referee> optionalReferee = rr.findByIdForUpdate(refereeId);    
        if (optionalReferee.isPresent()) {
            isValidName(newName);
            Referee updatedReferee = optionalReferee.get();
            updatedReferee.setName(newName);
            rr.save(updatedReferee);
        } else {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
    }
    
    /**
     * Updates a referee's certificate status.
     *
     * @param refereeId the ID of the referee to update
     * @param newCertificate the new certificate status
     * @throws InvalidUserDataException if the referee doesn't exist or is principal in matches when trying to revoke certificate
     */
    @Transactional
    public void handleUpdateRefereeCertificate(Long refereeId, boolean newCertificate) {
        Optional<Referee> optionalReferee = rr.findByIdForUpdate(refereeId);    
        if (optionalReferee.isPresent()) {
            Referee updatedReferee = optionalReferee.get();
            if (!newCertificate) {
                for (Match m: updatedReferee.getMatches()) {
                    if (m.getPrincipalRefereeId() == refereeId) {
                        throw new InvalidUserDataException("Cannot change certificate to false because referee has matches as a principal referee!");
                    }
                }
            }
            updatedReferee.setHasCertificate(newCertificate);
            rr.save(updatedReferee);
        } else {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
    }
    
    /**
     * Adds a match to a referee's match list.
     *
     * @param refereeId the ID of the referee to update
     * @param matchId the ID of the match to add
     * @throws InvalidUserDataException if referee or match doesn't exist, or referee is already assigned to the match
     */
    @Transactional
    public void handleUpdateAddRefereeMatch(Long refereeId, Long matchId) {
        Optional<Referee> optionalReferee = rr.findByIdForUpdate(refereeId);    
        if (optionalReferee.isPresent()) {
            Match newMatch = mr.findById(matchId).orElseThrow(() -> new InvalidUserDataException("Match with Id " + matchId + " does not exist!"));    
            Referee updatedReferee = optionalReferee.get();
            if (updatedReferee.getMatches() != null && updatedReferee.getMatches().contains(newMatch)) {
                throw new InvalidUserDataException("Referee already in the match!");
            }
            updatedReferee.addMatch(newMatch);
            newMatch.getReferees().add(updatedReferee);
            mr.save(newMatch);
            rr.save(updatedReferee);
        } else {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
    }
    
    /**
     * Removes a match from a referee's match list.
     *
     * @param refereeId the ID of the referee to update
     * @param matchId the ID of the match to remove
     * @throws InvalidUserDataException if referee or match doesn't exist, or referee isn't assigned to the match
     */
    @Transactional
    public void handleUpdateRemoveRefereeMatch(Long refereeId, Long matchId) {
        Optional<Referee> optionalReferee = rr.findByIdForUpdate(refereeId);    
        if (optionalReferee.isPresent()) {
            Referee updatedReferee = optionalReferee.get();
            Match newMatch = mr.findById(matchId).orElseThrow(() -> new InvalidUserDataException("Match with Id " + matchId + " does not exist!"));    
            removeMatchFromReferee(updatedReferee, newMatch);
            newMatch.getReferees().remove(updatedReferee);
            mr.save(newMatch);
            rr.save(updatedReferee);
        } else {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
    }
    
    /**
     * Updates a referee with the specified ID using the provided data.
     *
     * @param refereeId the ID of the referee to update
     * @param userData DTO containing the updated referee data
     * @throws InvalidUserDataException if the referee doesn't exist or data is invalid
     */
    @Transactional
    public void handleUpdateReferee(Long refereeId, RefereeDto userData) {
        Optional<Referee> optionalReferee = rr.findByIdForUpdate(refereeId);    
        if (optionalReferee.isPresent()) {
            Referee updatedReferee = optionalReferee.get();
            handleUpdateRefereeName(refereeId, userData.getName());
            handleUpdateRefereeCertificate(refereeId, userData.getHasCertificate());
            if (userData.getMatches() == null) {
                userData.setMatches(new ArrayList<>());
            }
            updateMatchesForReferee(updatedReferee, userData.getMatches(), updatedReferee.getMatchesIds());
            rr.save(updatedReferee);
        } else {
            throw new InvalidUserDataException("Referee with Id " + refereeId + " does not exists!");
        }
    }
    
    /**
     * Validates that a name is not empty or null.
     *
     * @param name the name to validate
     * @return true if the name is valid
     * @throws InvalidUserDataException if name is invalid
     */
    private static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserDataException("Name can not be empty!");
        }
        return true;
    }
    
    /**
     * Validates that a position string represents a valid Position enum value.
     *
     * @param positionString the position string to validate
     * @return true if the position is valid
     * @throws InvalidUserDataException if position is invalid
     */
    private static boolean isValidPlayerPosition(String positionString) {
        try {
            Position.valueOf(positionString);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserDataException("Player invalid position!");
        }
        return true;
    }
    
    /**
     * Retrieves valid Team entities from a list of team IDs.
     *
     * @param teams list of team IDs
     * @return list of valid Team entities
     * @throws InvalidUserDataException if any team ID is invalid
     */
    private List<Team> getValidPlayerTeams(List<Long> teams) {
        List<Team> validTeams = new ArrayList<>();
        if (teams != null && !teams.isEmpty() && !(teams.size() == 1 && teams.get(0) == 0)) {
            for (Long teamId: teams) {
                Optional<Team> optionalTeam = tr.findById(teamId);
                if (optionalTeam.isPresent()) {
                    validTeams.add(optionalTeam.get());
                } else {
                    throw new InvalidUserDataException("Team with Id " + teamId + " does not exist!");
                }
            }
        }
        return validTeams;
    }
    
    /**
     * Retrieves valid Match entities from a list of match IDs.
     *
     * @param matches list of match IDs
     * @return list of valid Match entities
     * @throws InvalidUserDataException if any match ID is invalid
     */
    private List<Match> getValidRefereeMatches(List<Long> matches) {
        List<Match> validMatches = new ArrayList<>();
        if (matches != null && !matches.isEmpty() && !(matches.size() == 1 && matches.get(0) == 0)) {
            for (Long matchId: matches) {
                Optional<Match> optionalMatch = mr.findById(matchId);
                if (optionalMatch.isPresent()) {
                    validMatches.add(optionalMatch.get());
                } else {
                    throw new InvalidUserDataException("Match with Id " + matchId + " does not exist!");
                }
            }
        }
        return validMatches;
    }
    
    /**
     * Validates that a player has no future matches scheduled.
     *
     * @param playerId the player ID to check
     * @return true if the player has no future matches
     * @throws InvalidUserDataException if the player has future matches
     */
    private boolean playerHasNotFutureMatches(Long playerId) {
        if (mr.hasUpcomingMatchesPlayer(playerId, LocalDate.now(), LocalTime.now())) {
            throw new InvalidUserDataException("Player has future matches to play!");
        }
        return true;
    }
    
    /**
     * Removes a player from all their teams.
     *
     * @param playerId the ID of the player to remove from teams
     */
    private void removePlayerFromTeams(Long playerId) {
        Player player = pr.findById(playerId).get();
        List<Team> teams = player.getTeams();
        if (teams != null) {
            for (Team t: teams) {
                t.getPlayers().remove(player);
                tr.save(t);
            }
        }
    }
    
    /**
     * Validates that a referee has no future matches to arbitrate.
     *
     * @param refereeId the referee ID to check
     * @return true if the referee has no future matches
     * @throws InvalidUserDataException if the referee has future matches
     */
    private boolean refereeHasNotFutureMatches(Long refereeId) {
        if (mr.hasUpcomingMatchesReferee(refereeId, LocalDate.now(), LocalTime.now())) {
            throw new InvalidUserDataException("Referee has future matches to arbitrate!");
        }
        return true;
    }
    
    /**
     * Removes a referee from all their matches.
     *
     * @param refereeId the ID of the referee to remove from matches
     * @throws InvalidUserDataException if the referee is a principal referee in any match
     */
    private void removeRefereeFromMatches(Long refereeId) {
        Referee referee = rr.findById(refereeId).get();
        List<Match> matches = referee.getMatches();
        if (matches != null) {
            for (Match m: matches) {
                if (m.getPrincipalRefereeId() == refereeId) {
                    throw new InvalidUserDataException("Referee is principal in some match!");
                }
                m.getReferees().remove(referee);
                mr.save(m);
            }
        }
    }
    
    /**
     * Removes a team from a player's team list.
     *
     * @param player the player to update
     * @param toRemoveTeam the team to remove
     * @throws InvalidUserDataException if the team has future matches or player isn't in the team
     */
    private void removeTeamFromPlayer(Player player, Team toRemoveTeam) {
        if (!mr.upcomingMatchesForTeams(new ArrayList<>(List.of(toRemoveTeam.getTeamId())), LocalDate.now(), LocalTime.now())) {
            List<Team> playerTeams = player.getTeams();
            if (playerTeams != null && playerTeams.contains(toRemoveTeam)) {
                playerTeams.remove(toRemoveTeam);
                toRemoveTeam.getPlayers().remove(player);
            } else {
                throw new InvalidUserDataException("Player is not in the team to remove!");
            }
        } else {
            throw new InvalidUserDataException("Player has future matches to play!");
        }
    }
    
    /**
     * Removes a match from a referee's match list.
     *
     * @param referee the referee to update
     * @param toRemoveMatch the match to remove
     * @throws InvalidUserDataException if the match is in the future or referee isn't assigned to the match
     */
    private void removeMatchFromReferee(Referee referee, Match toRemoveMatch) {
        if (!mr.hasUpcomingMatchesFromIds(new ArrayList<>(List.of(toRemoveMatch.getMatchId())), LocalDate.now(), LocalTime.now())) {
            List<Match> refereeMatches = referee.getMatches();
            if (toRemoveMatch.getPrincipalReferee().getUserId() != referee.getUserId()) {
            	if (refereeMatches != null && refereeMatches.contains(toRemoveMatch)) {
                    refereeMatches.remove(toRemoveMatch);
                    toRemoveMatch.getReferees().remove(referee);
                } else {
                    throw new InvalidUserDataException("Referee is not in the match to remove!");
                }
            } else {
            	throw new InvalidUserDataException("Can not remove principal referee from match!");
            }
        } else {
            throw new InvalidUserDataException("Referee has future matches to arbitrate!");
        }
    }
    
    /**
     * Updates the teams for a player based on the new team list.
     *
     * @param player the player to update
     * @param teamToUpdate the new list of team IDs
     * @param playerTeams the current list of team IDs
     */
    private void updateTeamsForPlayer(Player player, List<Long> teamToUpdate, List<Long> playerTeams) {
        List<Long> teamsToRemove = new ArrayList<>(playerTeams);
        List<Long> teamsToAdd = new ArrayList<>(teamToUpdate);
        teamsToRemove.removeAll(teamToUpdate);
        teamsToAdd.removeAll(playerTeams);
        
        for (Long t: teamsToRemove) {
            handleUpdateRemovePlayerTeam(player.getUserId(), t);
        }
        for (Long t: teamsToAdd) {
            handleUpdateAddPlayerTeam(player.getUserId(), t);
        }
    }
    
    /**
     * Updates the matches for a referee based on the new match list.
     *
     * @param referee the referee to update
     * @param matchesToUpdate the new list of match IDs
     * @param refereeMatches the current list of match IDs
     */
    private void updateMatchesForReferee(Referee referee, List<Long> matchesToUpdate, List<Long> refereeMatches) {
        List<Long> matchesToRemove = new ArrayList<>(refereeMatches);
        List<Long> matchesToAdd = new ArrayList<>(matchesToUpdate);
        matchesToRemove.removeAll(matchesToUpdate);
        matchesToAdd.removeAll(refereeMatches);
        
        for (Long m: matchesToRemove) {
            handleUpdateRemoveRefereeMatch(referee.getUserId(), m);
        }
        for (Long m: matchesToAdd) {
            handleUpdateAddRefereeMatch(referee.getUserId(), m);
        }
    }


    // -------------------- PLAYER FILTERING HANDLERS --------------------



    /**
     * Filters players based on goals scored.
     *
     * @param maxGoals maximum number of goals (inclusive)
     * @param minGoals minimum number of goals (inclusive)
     * @return list of PlayerDto matching the criteria
     * @throws InvalidUserDataException if goal counts are invalid
     */
    public List<ViewModelPlayer> handleFilterPlayersByGoalsScored(Integer maxGoals, Integer minGoals) {
        if ((minGoals != null && minGoals < 0) || (maxGoals != null && maxGoals < 0)) {
            throw new InvalidUserDataException("Goal count cannot be negative!");
        }
        if( minGoals != null && maxGoals != null && minGoals > maxGoals) {
            throw new InvalidUserDataException("Minimum goals cannot be greater than maximum goals!");
        }

        List<ViewModelPlayer> players = new ArrayList<>();
        List<Player> allPlayers = pr.findAll();

        for (Player player : allPlayers) {
            int goalsScored = gr.countGoalsByPlayer(player);

            if ((minGoals != null && goalsScored < minGoals) ||
                    (maxGoals != null && goalsScored > maxGoals)) {
                continue;
            }

            players.add(PlayerMapper.toViewModel(player));
        }

        return players;
    }

    /**
     * Filters players based on cards received.
     *
     * @param maxCards maximum number of cards (inclusive)
     * @param minCards minimum number of cards (inclusive)
     * @return list of PlayerDto matching the criteria
     * @throws InvalidUserDataException if card counts are invalid
     */
    public List<ViewModelPlayer> handleFilterPlayersByCardsReceived(Integer maxCards, Integer minCards) {
        if ((minCards != null && minCards < 0) || (maxCards != null && maxCards < 0)) {
            throw new InvalidUserDataException("Card count cannot be negative!");
        }
        if (minCards != null && maxCards != null && minCards > maxCards) {
            throw new InvalidUserDataException("Minimum cards cannot be greater than maximum cards!");
        }

        List<ViewModelPlayer> players = new ArrayList<>();
        List<Player> allPlayers = pr.findAll();

        for (Player player : allPlayers) {
            int cards = cr.countCardsByPlayer(player);

            if ((minCards != null && cards < minCards) ||
                    (maxCards != null && cards > maxCards)) {
                continue; // Skip player if outside the allowed range
            }

            players.add(PlayerMapper.toViewModel(player));
        }

        return players;
    }

    /**
     * Filters players based on the number of games played.
     *
     * @param maxGames maximum number of games played (inclusive)
     * @param minGames minimum number of games played (inclusive)
     * @return list of PlayerDto matching the criteria
     * @throws InvalidUserDataException if game counts are invalid
     */
    public List<ViewModelPlayer> handleFilterPlayersByGamesPlayed(Integer maxGames, Integer minGames) {
        // Validate negative values
        if ((maxGames != null && maxGames < 0) || (minGames != null && minGames < 0)) {
            throw new InvalidUserDataException("Games played cannot be negative!");
        }
        if (minGames != null && maxGames != null && minGames > maxGames) {
            throw new InvalidUserDataException("Minimum games cannot be greater than maximum games!");
        }

        List<ViewModelPlayer> players = new ArrayList<>();
        List<Player> allPlayers = pr.findAll();

        for (Player player : allPlayers) {
            int gamesPlayed = 0;

            for (Team team : player.getTeams()) {
                gamesPlayed += team.getMatchesAsTeam1().size();
                gamesPlayed += team.getMatchesAsTeam2().size();
            }

            // Apply filters
            if ((maxGames != null && gamesPlayed > maxGames) ||
                    (minGames != null && gamesPlayed < minGames)) {
                continue;
            }

            players.add(PlayerMapper.toViewModel(player));
        }

        return players;
    }

    /**
     * Filters players by name.
     *
     * @param name the name to filter players by
     * @return list of PlayerDto matching the name
     * @throws InvalidUserDataException if the name is empty or null
     */
    public List<ViewModelPlayer> handleFilterPlayersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserDataException("Name cannot be empty!");
        }
        List<Player> players = pr.findByNameContainingIgnoreCase(name);
        List<ViewModelPlayer> playersDto = new ArrayList<>();
        for (Player player : players) {
            playersDto.add(PlayerMapper.toViewModel(player));
        }
        return playersDto;
    }

    public List<ViewModelPlayer> handleFilterPlayersByPosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new InvalidUserDataException("Position cannot be empty!");
        }

        String positionFormatted = position.toUpperCase().trim();

        if (!Position.isValidPosition(positionFormatted)) {
            throw new InvalidUserDataException("Invalid position: " + position);
        }

        Position pos = Position.valueOf(positionFormatted);

        List<Player> players = pr.findByPreferredPosition(pos);

        return players.stream()
                .map(PlayerMapper::toViewModel)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all players.
     *
     * @return list of PlayerDto for all players
     */
    public List<ViewModelPlayer> handleFindAllPlayers() {
        List<Player> players = pr.findAll();
        List<ViewModelPlayer> playersDto = new ArrayList<>();
        for (Player player : players) {
            playersDto.add(PlayerMapper.toViewModel(player));
        }
        return playersDto;
    }

    // -------------------- REFEREE FILTERING HANDLERS --------------------

    /**
     * Filters referees based on the number of matches officiated.
     *
     * @param maxGames maximum number of matches officiated (inclusive)
     * @param minGames minimum number of matches officiated (inclusive)
     * @return list of RefereeDto matching the criteria
     * @throws InvalidUserDataException if game counts are invalid
     */
    public List<ViewModelReferee> handleFilterRefereesByMatchesOficialized(Integer maxGames, Integer minGames) {
        // Validate negative values
        if ((maxGames != null && maxGames < 0) || (minGames != null && minGames < 0)) {
            throw new InvalidUserDataException("Matches oficialized cannot be negative!");
        }
        if (minGames != null && maxGames != null && minGames > maxGames) {
            throw new InvalidUserDataException("Minimum matches cannot be greater than maximum matches!");
        }
        List<ViewModelReferee> referees = new ArrayList<>();
        List<Referee> allReferees = rr.findAll();
        for (Referee referee : allReferees) {
            int matchesOficialized = referee.getMatches().size();
            // Apply filters
            if ((maxGames != null && matchesOficialized > maxGames) ||
                    (minGames != null && matchesOficialized < minGames)) {
                continue;
            }
            referees.add(RefereeMapper.toViewModel(referee));
        }
        return referees;
    }

    /**
     * Filters referees based on the number of cards shown.
     *
     * @param maxCards maximum number of cards shown (inclusive)
     * @param minCards minimum number of cards shown (inclusive)
     * @return list of RefereeDto matching the criteria
     * @throws InvalidUserDataException if card counts are invalid
     */
    public List<ViewModelReferee> handleFilterRefereesByCardsShown(Integer maxCards, Integer minCards) {
        if ((maxCards != null && maxCards < 0) || (minCards != null && minCards < 0)) {
            throw new InvalidUserDataException("Cards shown cannot be negative!");
        }
        if (minCards != null && maxCards != null && minCards > maxCards) {
            throw new InvalidUserDataException("Minimum cards cannot be greater than maximum cards!");
        }

        List<ViewModelReferee> referees = new ArrayList<>();
        List<Referee> allReferees = rr.findAll();
        for (Referee referee : allReferees) {
            int cardsShown = 0;

            for (Match match : referee.getMatches()) {
                // assumo que só o árbitro principal é que dá cartões, o resto tá só a ver
                if(referee.equals(match.getPrincipalReferee())) {
                    MatchStatistics stats = match.getStats();
                    if (stats != null) {
                        cardsShown += cr.countCardsByMatchStatistics(stats);
                    }
                }
            }

            // Aplicar os filtros
            if ((maxCards != null && cardsShown > maxCards) ||
                    (minCards != null && cardsShown < minCards)) {
                continue;
            }
            referees.add(RefereeMapper.toViewModel(referee));
        }

        return referees;
    }

    /**
     * Filters referees by name.
     *
     * @param name the name to filter referees by
     * @return list of RefereeDto matching the name
     * @throws InvalidUserDataException if the name is empty or null
     */
    public List<ViewModelReferee> handleFilterRefereesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserDataException("Name cannot be empty!");
        }
        List<Referee> referees = rr.findByNameContainingIgnoreCase(name);
        List<ViewModelReferee> refereesDto = new ArrayList<>();
        for (Referee referee : referees) {
            refereesDto.add(RefereeMapper.toViewModel(referee));
        }
        return refereesDto;
    }

    /**
     * Retrieves all referees.
     *
     * @return list of RefereeDto for all referees
     */
    public List<ViewModelReferee> handleFindAllReferees() {
        List<Referee> referees = rr.findAll();
        List<ViewModelReferee> refereesDto = new ArrayList<>();
        for (Referee referee : referees) {
            refereesDto.add(RefereeMapper.toViewModel(referee));
        }
        return refereesDto;
    }
    
}