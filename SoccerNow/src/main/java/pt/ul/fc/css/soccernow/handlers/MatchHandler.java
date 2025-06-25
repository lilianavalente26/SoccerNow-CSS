package pt.ul.fc.css.soccernow.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.MatchStatisticsDto;
import pt.ul.fc.css.soccernow.entities.*;
import pt.ul.fc.css.soccernow.exceptions.InvalidClubDataException;
import pt.ul.fc.css.soccernow.exceptions.InvalidMatchDataException;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.mappers.MatchMapper;
import pt.ul.fc.css.soccernow.repositories.*;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelMatch;
import pt.ul.fc.css.soccernow.enums.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchHandler {

    private final MatchRepository mr;
    private final TeamRepository tr;
    private final StadiumRepository sr;
    private final RefereeRepository rr;
    private final PlayerRepository pr;
    private final MatchStatisticsRepository msr;
    private final GoalRepository gr;
    private final CardRepository cr;
    private final PointsTournamentRepository pointsTournamentRepository;

    /**
     * Constructs a MatchService with the necessary repositories.
     *
     * @param mr MatchRepository for match operations
     * @param cr ClubRepository for club operations
     * @param tr TeamRepository for team operations
     * @param sr StadiumRepository for stadium operations
     * @param rr RefereeRepository for referee operations
     * @param pr PlayerRepository for player operations
     * @param msr MatchStatisticsRepository for statistics operations
     * @param gr GoalRepository for goal operations
     */
    @Autowired
    public MatchHandler(MatchRepository mr,
                        TeamRepository tr,
                        StadiumRepository sr,
                        RefereeRepository rr,
                        PlayerRepository pr,
                        MatchStatisticsRepository msr,
                        GoalRepository gr,
                        CardRepository cr,
                        PointsTournamentRepository pointsTournamentRepository) {
        this.mr = mr;
        this.tr = tr;
        this.sr = sr;
        this.rr = rr;
        this.pr = pr;
        this.msr = msr;
        this.gr = gr;
        this.cr = cr;
        this.pointsTournamentRepository = pointsTournamentRepository;
    }

    /**
     * Registers a new match with the provided data.
     *
     * @param matchData DTO containing match registration data
     * @return the ID of the newly registered match
     * @throws InvalidMatchDataException if the match data is invalid
     */
    @Transactional
    public long handleRegisterMatch(MatchDto matchData) {

		Match match = new Match();

        // register referees
        registerReferees(matchData, match);
        //register Teams
        registerTeams(matchData, match);
        // register date
        LocalDate today = LocalDate.now();
        registerDate(matchData, today, match);
        // register time
        LocalTime timeNow = LocalTime.now();
        registerTime(matchData, timeNow, today, match);
        // register stadium
        registerStadium(matchData, match);
        // register stats
        match.setStats(new MatchStatistics());
        msr.save(match.getStats());

        // !Coming Soon :)
        Long tournamentId = matchData.getTournament();
        if (tournamentId != null) {
            PointsTournament tournament = pointsTournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new InvalidMatchDataException("Tournament with Id " + tournamentId + " does not exist!"));
            
            boolean validReferees = false;
            for (Referee referee: match.getReferees()) {
            	if (referee.isHasCertificate()) {
            		validReferees = true;
            	}
            }
            if (!validReferees) {
            	throw new InvalidClubDataException("Tournament match needs at least 1 referee with certificate!");
            }
            
            match.setTournament(tournament);

            mr.save(match);
            tournament.addMatch(match);
            pointsTournamentRepository.save(tournament);

        }

        mr.save(match);
        
        if (matchData.getReferees() != null) {
			for (Long refereeId : matchData.getReferees()) {
				Referee r = rr.findById(refereeId).get();
			    r.addMatch(match);
			    rr.save(r);
			}
		}
        
        return match.getMatchId();
    }

    /**
     * Retrieves match data for the specified match ID.
     *
     * @param id the ID of the match to retrieve
     * @return MatchDto containing the match data
     * @throws InvalidMatchDataException if the match doesn't exist
     */
    public MatchDto handleGetMatch(Long id) {
        if(id == null) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to retrieve a match");
        }
        else{
            if(!mr.existsById(id)) {
                throw new InvalidMatchDataException("Match does not exist in the database");
            }
            else{
                Match match = mr.findById(id).get();
                MatchDto matchData = new MatchDto();

                matchData.setPrincipalReferee(match.getPrincipalRefereeId());
                matchData.setReferees(match.getRefereesIds());
                matchData.setTeam1(match.getTeam1Id());
                matchData.setTeam2(match.getTeam2Id());
                matchData.setDate(match.getDate());
                matchData.setTime(match.getTime());
                matchData.setStadium(match.getStadiumId());
                matchData.setStats(match.getStatsId());
                if (match.getTournament() != null) {
                    matchData.setTournament(match.getTournamentId());
                } else {
                	matchData.setTournament(null);
                }

                return matchData;
            }
        }
    }

    /**
     * Deletes a match with the specified ID.
     *
     * @param id the ID of the match to delete
     * @throws InvalidMatchDataException if the match doesn't exist or has goals recorded
     */
    @Transactional
    public void handleDeleteMatch(Long id) {
        Match match = mr.findById(id).orElseThrow(() ->
                new InvalidMatchDataException("Match does not exist in the database, so it cannot be deleted.")
        );

        // Verifica se há golos associados ao jogo
        List<Goal> goals = gr.findByMatch(match);
        if (!goals.isEmpty()) {
            throw new InvalidMatchDataException("Cannot delete a match that has already been played (goals exist).");
        }

        // Apagar estatísticas se quiseres (ou manter)
        if (match.getStats() != null) {
            msr.delete(match.getStats());
        }

        mr.delete(match);
    }

    /**
     * Registers a goal for a player in a specified match.
     *
     * @param matchId the ID of the match
     * @param playerId the ID of the player who scored
     * @throws InvalidMatchDataException if match/player doesn't exist or player isn't in match
     */
    @Transactional
    public void handleRegisterGoal(Long matchId, Long playerId) { 	
        if(matchId == null ) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to register a goal");
        } else if (playerId == null) {
            throw new InvalidMatchDataException("Player field needs to exist in order to register a goal");
        } else{

            // ir buscar o match
            Match match = getMatchByIdUpdate(matchId);
            
            if (match.getStats().isOver()) {
            	throw new InvalidMatchDataException("Can not register goal because the match is over!");
            }

            // ir buscar o jogador
            Player player = getPlayerByID(playerId);

            // ir buscar as stats do match
            MatchStatistics stats = getMatchStatistics(match);

            // verifica se o jogador pertence a alguma das equipas
            // se o jogador pertence à equipa 1
            if (match.getTeam1().getPlayers().contains(player)) {
                int goalsTeam1 = stats.getTeam1_Score();

                // incrementar o número de golos da equipa 1
                stats.setTeam1_Score(goalsTeam1 + 1);

            }
            // se o jogador pertence à equipa 2
            else if (match.getTeam2().getPlayers().contains(player)) {
                int goalsTeam2 = stats.getTeam2_Score();

                // incrementar o número de golos da equipa 2
                stats.setTeam2_Score(goalsTeam2 + 1);

            } else {
                throw new InvalidMatchDataException("Player does not belong to any of the teams in the match");
            }

            // criar o goal
            Goal goal = new Goal();
            goal.setPlayer(player);
            goal.setMatch(match);
            goal.setMatchStatistics(stats);
            gr.save(goal);
            
            if (stats.getGoals() == null) {
            	stats.setGoals(new ArrayList<>());
            }
            stats.getGoals().add(goal);
            msr.save(stats);
            
        }
    }

    /**
     * Marks a match as finished and determines the winner.
     *
     * @param matchId the ID of the match to finish
     * @return MatchStatisticsDto with the final match statistics
     * @throws InvalidMatchDataException if the match doesn't exist or is already finished
     */
    @Transactional
    public void handleSetMatchStateAsFinished(Long matchId) {
        if(matchId == null) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to set the match as finished");
        } else{
            Match match = getMatchByIdUpdate(matchId);

            MatchStatistics stats = getMatchStatistics(match);

            if(stats.isOver()){
                throw new InvalidMatchDataException("Match is already finished");
            } else {
                stats.setOver(true);

                // Calcular o vencedor
                if (stats.getTeam1_Score() > stats.getTeam2_Score()) {
                    stats.setWinnerTeam(1);
                } else if (stats.getTeam1_Score() < stats.getTeam2_Score()) {
                    stats.setWinnerTeam(2);
                } else {
                    stats.setWinnerTeam(null); // Empate manda-se o vencedor null
                }
                msr.save(stats);
                
                if (match.getTournament() != null) {
                	if (match.getTournament() instanceof PointsTournament) {
                		PointsTournament pt = (PointsTournament) match.getTournament();
                		if (stats.getWinnerTeam() != null) {
                			if (stats.getWinnerTeam() == 1) {
                    			pt.updateClubStandings(match.getTeam1().getClub(), true, stats.getTeam1_Score(), stats.getTeam2_Score());
                			} else {
                    			pt.updateClubStandings(match.getTeam2().getClub(), true, stats.getTeam2_Score(), stats.getTeam1_Score());
                			}
                		} else {
                			pt.updateClubStandings(match.getTeam1().getClub(), null, stats.getTeam1_Score(), stats.getTeam2_Score());
                			pt.updateClubStandings(match.getTeam2().getClub(), null, stats.getTeam2_Score(), stats.getTeam1_Score());
                		}
                        pointsTournamentRepository.save(pt);
                	}
                }
                
            }
        }
    }

    /**
     * Registers a yellow card for a player in a specified match.
     *
     * @param matchId the ID of the match
     * @param playerId the ID of the player receiving the card
     * @throws InvalidMatchDataException if match/player doesn't exist or player isn't in match
     */
    @Transactional
    public void handleRegisterYellowCard(Long matchId, Long playerId) {
        if(matchId == null ) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to register a card");
        } else if (playerId == null) {
            throw new InvalidMatchDataException("Player field needs to exist in order to register a card");
        } else {

            // ir buscar o match
            Match match = getMatchByIdUpdate(matchId);
            
            if (match.getStats().isOver()) {
            	throw new InvalidMatchDataException("Can not register card because the match is over!");
            }

            // ir buscar o jogador
            Player player = getPlayerByID(playerId);

            // ir buscar as stats do match
            MatchStatistics stats = getMatchStatistics(match);

            // verifica se o jogador pertence a alguma das equipas
            // se o jogador pertence à equipa 1
            // Jogador pertence à equipa 1
            if (match.getTeam1().getPlayers().contains(player)) {
            	System.out.println("ENTREI NO 1 IF");
                Card card = new Card();
                card.setPlayer(player);
                card.setCardType(CardType.YELLOW_CARD);
                card.setMatchStatistics(stats);
                card.setTeam(1);
                
                if (stats.getCards() == null) {
                	stats.setCards(new ArrayList<>());
                }
                stats.getCards().add(card);
                cr.save(card);
            }

            // Jogador pertence à equipa 2
            else if (match.getTeam2().getPlayers().contains(player)) {
            	System.out.println("ENTREI NO 2 IF");
                Card card = new Card();
                card.setPlayer(player);
                card.setCardType(CardType.YELLOW_CARD);
                card.setMatchStatistics(stats);
                card.setTeam(2);
                
                if (stats.getCards() == null) {
                	stats.setCards(new ArrayList<>());
                }
                stats.getCards().add(card);
                cr.save(card);
            } else {
                throw new InvalidMatchDataException("Player does not belong to any of the teams in the match");
            }
            msr.save(stats);
        }
    }

    /**
     * Registers a red card for a player in a specified match.
     *
     * @param matchId the ID of the match
     * @param playerId the ID of the player receiving the card
     * @throws InvalidMatchDataException if match/player doesn't exist or player isn't in match
     */
    @Transactional
    public void handleRegisterRedCard(Long matchId, Long playerId) {
        if(matchId == null ) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to register a card");
        } else if (playerId == null) {
            throw new InvalidMatchDataException("Player field needs to exist in order to register a card");
        } else {

            // ir buscar o match
            Match match = getMatchByIdUpdate(matchId);
            
            if (match.getStats().isOver()) {
            	throw new InvalidMatchDataException("Can not register card because the match is over!");
            }

            // ir buscar o jogador
            Player player = getPlayerByID(playerId);

            // ir buscar as stats do match
            MatchStatistics stats = getMatchStatistics(match);

            if (match.getTeam1().getPlayers().contains(player)) {
                Card card = new Card();
                card.setPlayer(player);
                card.setCardType(CardType.RED_CARD);
                card.setMatchStatistics(stats);
                card.setTeam(1);
                
                if (stats.getCards() == null) {
                	stats.setCards(new ArrayList<>());
                }
                stats.getCards().add(card);
                cr.save(card);
            } else if (match.getTeam2().getPlayers().contains(player)) {
                Card card = new Card();
                card.setPlayer(player);
                card.setCardType(CardType.RED_CARD);
                card.setMatchStatistics(stats);
                card.setTeam(2);
                
                if (stats.getCards() == null) {
                	stats.setCards(new ArrayList<>());
                }
                stats.getCards().add(card);
                cr.save(card);
            } else {
                throw new InvalidMatchDataException("Player does not belong to any of the teams in the match");
            }
            msr.save(stats);
        }
    }
    
    /**
     * Retrieves a formated match form id
     * 
     * @param matchId match id
     * @return formatedDto match
     */
    public ViewModelMatch getFormatedMatch(Long matchId) {
    	Optional<Match> optionalMatch = mr.findById(matchId);
    	if (optionalMatch.isPresent()) {
    		return MatchMapper.toViewModel(optionalMatch.get());
    	} else {
    		throw new InvalidMatchDataException("Match does not exist in the database");
    	}
    }
    
    /**
     * Gets all the matches
     * 
     * @return all matches in a formatedDto
     */
    public List<ViewModelMatch> handleGetAllMatches() {
    	List<Match> matches = mr.findAll();
    	return matches.stream().map(m -> MatchMapper.toViewModel(m)).collect(Collectors.toList());
    }

    /**
     * Helper method to retrieve a match by its ID.
     *
     * @param matchId the ID of the match to retrieve
     * @return the Match entity
     * @throws InvalidMatchDataException if the match doesn't exist
     */
    private Match getMatchByIdUpdate(Long matchId) {
        Match match = mr.findByIdForUpdate(matchId).isPresent() ? mr.findById(matchId).get() : null;
        if (match == null) {
            throw new InvalidMatchDataException("Match does not exist in the database");
        }
        return match;
    }
    
    private Match getMatchById(Long matchId) {
        Match match = mr.findById(matchId).isPresent() ? mr.findById(matchId).get() : null;
        if (match == null) {
            throw new InvalidMatchDataException("Match does not exist in the database");
        }
        return match;
    }

    /**
     * Helper method to retrieve a player by ID.
     *
     * @param playerId the ID of the player to retrieve
     * @return the Player entity
     * @throws InvalidMatchDataException if the player doesn't exist
     */
    private Player getPlayerByID(Long playerId) {
        Player player = pr.findById(playerId).isPresent() ? pr.findById(playerId).get() : null;
        if (player == null) {
            throw new InvalidMatchDataException("Player does not exist in the database");
        }
        return player;
    }

    /**
     * Helper method to retrieve match statistics from a match.
     *
     * @param match the Match entity to get statistics from
     * @return the MatchStatistics entity
     * @throws InvalidMatchDataException if the match has no statistics
     */
    private static MatchStatistics getMatchStatistics(Match match) {
        MatchStatistics stats = match.getStats();
        if (stats == null) {
            throw new InvalidMatchDataException("Match does not have stats");
        }
        return stats;
    }

    /**
     * Helper method to register a stadium for a match.
     *
     * @param matchData DTO containing match registration data
     * @param match the Match entity to register the stadium for
     * @throws InvalidMatchDataException if the stadium doesn't exist
     */
    private void registerStadium(MatchDto matchData, Match match) {
        if(!sr.existsById(matchData.getStadium())) {
            throw new InvalidMatchDataException("Stadium does not exist in the database");
        }
        else{
            match.setStadium(sr.findById(matchData.getStadium()).get());
        }
    }

    /**
     * Helper method to register the time for a match with validation.
     *
     * @param matchData DTO containing match registration data
     * @param timeNow current time for validation
     * @param today current date for validation
     * @param match the Match entity to register the time for
     * @throws InvalidMatchDataException if time is invalid or in the past
     */
    private static void registerTime(MatchDto matchData, LocalTime timeNow, LocalDate today, Match match) {
        if(matchData.getTime() == null) {
            throw new InvalidMatchDataException("Time field needs to exist");
        }
        else{
            if(matchData.getTime().isBefore(timeNow) && matchData.getDate().equals(today)) {
                throw new InvalidMatchDataException("Cannot register a match that already took place earlier today");
            }
            else{
                match.setTime(matchData.getTime());
            }
        }
    }

    /**
     * Helper method to register the date for a match with validation.
     *
     * @param matchData DTO containing match registration data
     * @param today current date for validation
     * @param match the Match entity to register the date for
     * @throws InvalidMatchDataException if date is invalid or in the past
     */
    private static void registerDate(MatchDto matchData, LocalDate today, Match match) {
        if(matchData.getDate() == null) {
            throw new InvalidMatchDataException("Date field needs to exist");
        }
        else{
            if(matchData.getDate().isBefore(today)) {
                throw new InvalidMatchDataException("Cannot register a match that already took place");
            }
            else{
                match.setDate(matchData.getDate());
            }
        }
    }

    /**
     * Helper method to register referees for a match with validation.
     *
     * @param matchData DTO containing match registration data
     * @param match the Match entity to register referees for
     * @throws InvalidMatchDataException if referees data is invalid
     */
    private void registerReferees(MatchDto matchData, Match match) {
        // register referees
        if (matchData.getReferees() == null || matchData.getReferees().isEmpty()) {
            throw new InvalidMatchDataException("Referees field needs to contain at least one referee");
        }

        List<Referee> referees = new ArrayList<>();
        for (Long refereeId : matchData.getReferees()) {
            if (!rr.existsById(refereeId)) {
                throw new InvalidMatchDataException("Referee with ID " + refereeId + " does not exist in the database");
            }
            referees.add(rr.findById(refereeId).get());
        }
        match.setReferees(referees);

        // determinar o principal referee
        if (matchData.getReferees().size() == 1 && matchData.getReferees().get(0) == matchData.getPrincipalReferee()) {
            // só há um, é fica definido como o principal
            match.setPrincipalReferee(referees.get(0));
        } else {
            // há mais de um tenho de especificar o principal
            if (matchData.getPrincipalReferee() == null || matchData.getPrincipalReferee() == 0) {
                throw new InvalidMatchDataException("Must specify a principal referee when there is more than one referee");
            }

            Referee principal = rr.findById(matchData.getPrincipalReferee())
                    .orElseThrow(() -> new InvalidMatchDataException("Principal referee does not exist in the database"));

            if (!referees.contains(principal)) {
                throw new InvalidMatchDataException("Principal referee must be included in the list of referees");
            }

            match.setPrincipalReferee(principal);
        }
    }

    /**
     * Helper method to register teams for a match with validation.
     *
     * @param matchData DTO containing match registration data
     * @param match the Match entity to register teams for
     * @throws InvalidMatchDataException if teams data is invalid
     */
    private void registerTeams(MatchDto matchData, Match match) {
        if(matchData.getTeam1() == null) {
            throw new InvalidMatchDataException("Team 1 field needs to exist");
        }
        else if(matchData.getTeam2() == null) {
            throw new InvalidMatchDataException("Team 2 field needs to exist");
        }
        else{
        	if (matchData.getTeam1() == matchData.getTeam2()) {
        		throw new InvalidMatchDataException("A match cannot have the same team for both sides!");
        	}
            // verifica se a equipa existe na db
            if(!tr.existsById(matchData.getTeam1())) {
                throw new InvalidMatchDataException("Team 1 does not exist in the database");
            }
            else{
                // aqui tenho de fazer .get() para ir buscar o objeto em si e não só o id
                match.setTeam1(tr.findById(matchData.getTeam1()).get());
            }
            // verifica se a equipa existe na db
            if(!tr.existsById(matchData.getTeam2())) {
                throw new InvalidMatchDataException("Team 2 does not exist in the database");
            }
            else{
                match.setTeam2(tr.findById(matchData.getTeam2()).get());
            }
        }
    }

    /**
     * Retrieves comprehensive statistics for a specified match.
     *
     * @param matchId the ID of the match
     * @return formatted string containing all match statistics
     * @throws InvalidMatchDataException if the match doesn't exist
     */
    public String handleGetAllMatchStatistics(Long matchId) {
        if(matchId == null) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to retrieve the match statistics");
        } else{
            Match match = getMatchById(matchId);

            MatchStatistics stats = getMatchStatistics(match);

            StringBuilder allMatchStatistics = new StringBuilder();
            allMatchStatistics.append("Match ID: ").append(match.getMatchId()).append("\n");
            allMatchStatistics.append("Match Date: ").append(match.getDate()).append("\n");
            allMatchStatistics.append("Match Time: ").append(match.getTime()).append("\n");
            allMatchStatistics.append("Stadium: ").append(match.getStadium().getStadiumName()).append("\n");
            allMatchStatistics.append("Team 1: ").append(match.getTeam1().getClub().getNameClub()).append("\n");
            allMatchStatistics.append("Team 2: ").append(match.getTeam2().getClub().getNameClub()).append("\n");
            allMatchStatistics.append("Winner Team: ");
            if(stats.getWinnerTeam() == null && !stats.isOver()) {
                allMatchStatistics.append("No winner yet").append("\n");
            }
            else if(stats.getWinnerTeam() == null && stats.isOver()){
                allMatchStatistics.append("Draw").append("\n");
            }
            else if(stats.getWinnerTeam() == 1) {
                allMatchStatistics.append(match.getTeam1().getClub().getNameClub()).append("\n");
            }
            else if(stats.getWinnerTeam() == 2) {
                allMatchStatistics.append(match.getTeam2().getClub().getNameClub()).append("\n");
            }

            allMatchStatistics.append("Team 1 Score: ").append(stats.getTeam1_Score()).append("\n");
            allMatchStatistics.append("Team 2 Score: ").append(stats.getTeam2_Score()).append("\n");
            
            if (stats.getCards() == null) {
            	stats.setCards(new ArrayList<>());
            }
            allMatchStatistics.append("Team 1 Red Cards:").append("\n");
            for(Card card : stats.getCards().stream().filter(c -> c.getTeam() == 1).collect(Collectors.toList())) {
                if(card.getCardType() == CardType.RED_CARD)
                    allMatchStatistics.append(card.getPlayer().getName()).append("\n");
            }
            
            allMatchStatistics.append("Team 2 Red Cards:").append("\n");
            for(Card card : stats.getCards().stream().filter(c -> c.getTeam() == 2).collect(Collectors.toList())) {
                if(card.getCardType() == CardType.RED_CARD)
                    allMatchStatistics.append(card.getPlayer().getName()).append("\n");
            }
            
            allMatchStatistics.append("Team 1 Yellow Cards:").append("\n");
            for(Card card : stats.getCards().stream().filter(c -> c.getTeam() == 1).collect(Collectors.toList())) {
                if(card.getCardType() == CardType.YELLOW_CARD)
                    allMatchStatistics.append(card.getPlayer().getName()).append("\n");
            }
            
            allMatchStatistics.append("Team 2 Yellow Cards:").append("\n");
            for(Card card : stats.getCards().stream().filter(c -> c.getTeam() == 2).collect(Collectors.toList())) {
                if(card.getCardType() == CardType.YELLOW_CARD)
                    allMatchStatistics.append(card.getPlayer().getName()).append("\n");
            }
            
            allMatchStatistics.append("Goals:").append("\n");
            if (stats.getGoals() == null) {
            	stats.setGoals(new ArrayList<>());
            }
            for(Goal goal : stats.getGoals()) {
                allMatchStatistics.append(goal.getPlayer().getName()).append("\n");
            }

            return allMatchStatistics.toString();
        }
    }

    /**
     * Retrieves match statistics for a specified match ID.
     *
     * @param matchId the ID of the match
     * @return MatchStatisticsDto containing the match statistics
     * @throws InvalidMatchDataException if the match doesn't exist or has no statistics
     */
    public MatchStatisticsDto handleGetMatchStatistics(Long matchId) {
        if(matchId == null) {
            throw new InvalidMatchDataException("Match id field needs to exist in order to retrieve the match statistics");
        } else{
            Match match = getMatchById(matchId);

            MatchStatistics stats = getMatchStatistics(match);

            MatchStatisticsDto matchStats = new MatchStatisticsDto();
            matchStats.setMatchStatisticsId(stats.getMatchStatisticsId());
            matchStats.setTeam1_Score(stats.getTeam1_Score());
            matchStats.setTeam2_Score(stats.getTeam2_Score());
            matchStats.setWinnerTeam(stats.getWinnerTeam());
            if (stats.getCards() == null) {
            	stats.setCards(new ArrayList<>());
            }
        	matchStats.setTeam1_Cards(
                    stats.getCards().stream()
                            .filter(card -> card.getTeam() == 1)
                            .map(Card::getCardId)
                            .collect(Collectors.toList())
            );
            matchStats.setTeam2_Cards(
                    stats.getCards().stream()
                            .filter(card -> card.getTeam() == 2)
                            .map(Card::getCardId)
                            .collect(Collectors.toList())
            );
            
            if (stats.getGoals() == null) {
            	stats.setGoals(new ArrayList<>());
            }

        	matchStats.setGoals(
                    stats.getGoals().stream()
                            .map(Goal::getGoalId)
                            .collect(Collectors.toList())
            );
            matchStats.setOver(stats.isOver());

            return matchStats;
        }
    }

    /**
     * Handles searching and filtering matches based on various criteria.
     *
     * @param isOver whether the match is over
     * @param minGoals minimum number of goals scored in the match
     * @param maxGoals maximum number of goals scored in the match
     * @param stadiumName name of the stadium where the match was played
     * @param matchPeriod time period of the match (MORNING, AFTERNOON, NIGHT)
     * @return List of MatchDto containing filtered matches
     */
    public List<ViewModelMatch> handleSearchFilterMatch(Boolean isOver, Integer minGoals, Integer maxGoals, String stadiumName, String matchPeriod) {
    	List<Match> matches = mr.findAll();
    	
    	if (isOver != null) {
    		matches = filterByIsOver(matches, isOver);
    	}
    	if (minGoals != null) {
    		matches = filterByMinGoals(matches, minGoals);
    	}
    	if (maxGoals != null) {
    		matches = filterByMaxGoals(matches, maxGoals);
    	}
    	if (stadiumName != null) {
    		matches = filterByStadium(matches, stadiumName);
    	}
    	if (matchPeriod != null) {
    		isValidMatchPeriod(matchPeriod);
    		matches = filterByMatchPeriod(matches, matchPeriod);
    	}
    	return matches.stream().map(m -> MatchMapper.toViewModel(m)).collect(Collectors.toList());
    }

    /**
     * Validates the match period input.
     *
     * @param matchPeriod the match period to validate
     * @return true if valid, otherwise throws InvalidMatchDataException
     * @throws InvalidMatchDataException if the match period is invalid
     */
    private static boolean isValidMatchPeriod(String matchPeriod) {
    	try {
            MatchPeriod.valueOf(matchPeriod);
        } catch (IllegalArgumentException e) {
            throw new InvalidMatchDataException("Only accepts match period: MORNING, AFTERNOON and NIGHT!");
        }
        return true;
    }

    /**
     * Filters matches based on whether they are over.
     *
     * @param matches the list of matches to filter
     * @param isOver whether the match is over
     * @return List of filtered matches
     */
    private List<Match> filterByIsOver(List<Match> matches, Boolean isOver) {
    	return matches.stream().filter(m -> m.getStats().isOver() == isOver).collect(Collectors.toList());
    }

    /**
     * Filters matches based on the minimum number of goals scored.
     *
     * @param matches the list of matches to filter
     * @param minGoals the minimum number of goals
     * @return List of filtered matches
     */
    private List<Match> filterByMinGoals(List<Match> matches, Integer minGoals) {
    	return matches.stream().filter(m -> {
    		int totalGoals = 0;
    		totalGoals += m.getStats().getTeam1_Score();
    		totalGoals += m.getStats().getTeam2_Score();
    		return totalGoals >= minGoals;
    	})
    	.collect(Collectors.toList());
    }

    /**
     * Filters matches based on the maximum number of goals scored.
     *
     * @param matches the list of matches to filter
     * @param maxGoals the maximum number of goals
     * @return List of filtered matches
     */
    private List<Match> filterByMaxGoals(List<Match> matches, Integer maxGoals) {
    	return matches.stream().filter(m -> {
    		int totalGoals = 0;
    		totalGoals += m.getStats().getTeam1_Score();
    		totalGoals += m.getStats().getTeam2_Score();
    		return totalGoals <= maxGoals;
    	})
    	.collect(Collectors.toList());
    }

    /**
     * Filters matches based on the stadium name.
     *
     * @param matches the list of matches to filter
     * @param stadiumName the name of the stadium
     * @return List of filtered matches
     */
    private List<Match> filterByStadium(List<Match> matches, String stadiumName) {
    	return matches.stream().filter(m -> m.getStadium().getStadiumName().equals(stadiumName)).collect(Collectors.toList());
    }

    /**
     * Filters matches based on the match period (MORNING, AFTERNOON, NIGHT).
     *
     * @param matches the list of matches to filter
     * @param matchPeriod the match period to filter by
     * @return List of filtered matches
     */
    private List<Match> filterByMatchPeriod(List<Match> matches, String matchPeriod) {
    	return matches.stream().filter(m -> {
    		LocalTime matchTime = m.getTime();
    		MatchPeriod currentPeriod = null;
    		int hour = matchTime.getHour();
    		if (hour >= 6 && hour < 12) {
    			currentPeriod = MatchPeriod.MORNING;
    		} else if (hour >= 12 && hour < 18) {
    			currentPeriod = MatchPeriod.AFTERNOON;
    		} else {
    			currentPeriod = MatchPeriod.NIGHT;
    		}
    		return matchPeriod.equals(currentPeriod.toString());
    	})
    	.collect(Collectors.toList());
    }
    
}