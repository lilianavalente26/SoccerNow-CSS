package pt.ul.fc.css.soccernow;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.controllers.ClubController;
import pt.ul.fc.css.soccernow.controllers.MatchController;
import pt.ul.fc.css.soccernow.controllers.StadiumController;
import pt.ul.fc.css.soccernow.controllers.TeamController;
import pt.ul.fc.css.soccernow.controllers.TournamentController;
import pt.ul.fc.css.soccernow.controllers.UserController;
import pt.ul.fc.css.soccernow.dto.ClubDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.PlayerDto;
import pt.ul.fc.css.soccernow.dto.RefereeDto;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.dto.TournamentDto;

@SpringBootApplication
public class SoccerNowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoccerNowApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner demo(
    		UserController uc, 
    		TeamController tc,
    		ClubController cc,
    		StadiumController sc,
    		MatchController mc,
    		TournamentController ptc
    	){ return (args) -> {
    	
        System.out.println("Do some sanity tests here!");     

        final String[] allPlayersPositions = {"GOALKEEPER", "DEFENDER", "MIDFIELDER", "FORWARD"};
        
        String[] playerNames = {
                "Tiago Silva", "João Mendes", "Rui Gonçalves", "Diogo Matos", "Filipe Almeida",
                "Miguel Rocha", "André Pires", "Carlos Teixeira", "Pedro Faria", "Hugo Barbosa",
                "Bruno Costa", "Daniel Azevedo", "Ricardo Lopes", "Sérgio Freitas", "Marco Pinto",
                "Nuno Ferreira", "Vítor Antunes", "Paulo Moreira", "Leonardo Sá", "Artur Nogueira",
                "Denis Bahnari"
            };
        
        // Add 20 players
        System.out.println();
        for (int i = 1; i <= 20; i++) {
            PlayerDto player = new PlayerDto();
            player.setName(playerNames[i]);
            player.setPreferedPosition(allPlayersPositions[(i % 4)]);
            
            long playerId = uc.registerPlayer(player).getBody();
            System.out.println("Registered Player ID: " + playerId);
        }
        
        String[] refereeNames = {
                "Álvaro Ramos", "Bruno Martins", "César Neves", "Eduardo Tavares",
                "Fernando Reis", "Gilberto Matias", "Henrique Duarte", "Ícaro Vale"
            };

        // Add 6 referees
        System.out.println();
        for (int i = 1; i <= 6; i++) {
            RefereeDto referee = new RefereeDto();
            referee.setName(refereeNames[i]);
            referee.setHasCertificate(i%2==0 ? true : false);

            long refereeId = uc.registerReferee(referee).getBody();
            System.out.println("Registered Referee ID: " + refereeId);
        }
        
        // Add 4 stadiums
        System.out.println();
        long stadiumId1 = sc.registerStadium("Estádio de Alvalade").getBody();
        long stadiumId2 = sc.registerStadium("Estádio da Luz").getBody();
        long stadiumId3 = sc.registerStadium("Estádio de Braga").getBody();
        long stadiumId4 = sc.registerStadium("Estádio do Dragão").getBody();
        System.out.println("Registered Stadium ID: " + stadiumId1);
        System.out.println("Registered Stadium ID: " + stadiumId2);
        System.out.println("Registered Stadium ID: " + stadiumId3);
        System.out.println("Registered Stadium ID: " + stadiumId4);
        
        String[] clubNames = {
                "Sporting Clube de Lisboa", "Futebol Clube do Porto", "Sport Lisboa e Benfica",
                "Vitória de Guimarães", "SC Braga", "Belenenses SAD", "Boavista FC",
                "Rio Ave FC", "CD Tondela", "Marítimo", "Estoril Praia", "Casa Pia AC"
            };
        
        // Add 10 clubs
        System.out.println();
        for (int i = 0; i < 10; i++) {
        	long clubId = cc.registerClub(clubNames[i]).getBody();
            System.out.println("Registered Club ID: " + clubId);
        }
        
        // Add 4 teams
        System.out.println();
        TeamDto teamDto1 = new TeamDto();
        teamDto1.setClub(1l);
        teamDto1.setPlayers(new ArrayList<>(List.of(1l,2l,3l,4l,5l)));
        teamDto1.setGoalkeeper(4l);
        TeamDto teamDto2 = new TeamDto();
        teamDto2.setClub(2l);
        teamDto2.setPlayers(new ArrayList<>(List.of(6l,7l,8l,9l,10l)));
        teamDto2.setGoalkeeper(8l);
        TeamDto teamDto3 = new TeamDto();
        teamDto3.setClub(3l);
        teamDto3.setPlayers(new ArrayList<>(List.of(11l,12l,13l,14l,15l)));
        teamDto3.setGoalkeeper(12l);
        TeamDto teamDto4 = new TeamDto();
        teamDto4.setClub(4l);
        teamDto4.setPlayers(new ArrayList<>(List.of(16l,17l,18l,19l,20l)));
        teamDto4.setGoalkeeper(16l);
        
        long teamId1 = tc.registerTeam(teamDto1).getBody();
        long teamId2 = tc.registerTeam(teamDto2).getBody();
        long teamId3 = tc.registerTeam(teamDto3).getBody();
        long teamId4 = tc.registerTeam(teamDto4).getBody();
        System.out.println("Registered Team ID: " + teamId1);
        System.out.println("Registered Team ID: " + teamId2);
        System.out.println("Registered Team ID: " + teamId3);
        System.out.println("Registered Team ID: " + teamId4);
        
        // Add 1 tournament
        System.out.println();
        TournamentDto tDto = new TournamentDto();
        tDto.setTournamentName("Champions League");
        tDto.setClubs(new ArrayList<Long>(List.of(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l)));
        tDto.setOver(false);
        
        long tId = ptc.registerTournament(tDto).getBody();
        System.out.println("Registered Tournament ID: " + tId);
        
        // Add 4 matches
        System.out.println();
        MatchDto matchDto1 = new MatchDto();
        MatchDto matchDto2 = new MatchDto();
        MatchDto matchDto3 = new MatchDto();
        MatchDto matchDto4 = new MatchDto();
        
        matchDto1.setPrincipalReferee(22l);
        matchDto1.setReferees(new ArrayList<Long>(List.of(21l,22l)));
        matchDto1.setTeam1(1l);
        matchDto1.setTeam2(2l);
        matchDto1.setDate(LocalDate.now());
        matchDto1.setTime(LocalTime.now().plusSeconds(5));
        matchDto1.setStadium(1l);
        matchDto1.setTournament(1l);
        
        matchDto2.setPrincipalReferee(24l);
        matchDto2.setReferees(new ArrayList<Long>(List.of(21l,22l,24l)));
        matchDto2.setTeam1(2l);
        matchDto2.setTeam2(1l);
        matchDto2.setDate(LocalDate.now());
        matchDto2.setTime(LocalTime.now().plusSeconds(5));
        matchDto2.setStadium(2l);
        matchDto2.setTournament(1l);
        
        matchDto3.setPrincipalReferee(26l);
        matchDto3.setReferees(new ArrayList<Long>(List.of(22l,23l,26l)));
        matchDto3.setTeam1(3l);
        matchDto3.setTeam2(4l);
        matchDto3.setDate(LocalDate.now().plusDays(1));
        matchDto3.setTime(LocalTime.now());
        matchDto3.setStadium(3l);
        matchDto3.setTournament(1l);
        
        matchDto4.setPrincipalReferee(22l);
        matchDto4.setReferees(new ArrayList<Long>(List.of(21l,22l,25l)));
        matchDto4.setTeam1(4l);
        matchDto4.setTeam2(3l);
        matchDto4.setDate(LocalDate.now().plusDays(1));
        matchDto4.setTime(LocalTime.now());
        matchDto4.setStadium(4l);
        matchDto4.setTournament(1l);
        
        long match1Id = mc.registerMatch(matchDto1).getBody();
        long match2Id = mc.registerMatch(matchDto2).getBody();
        long match3Id = mc.registerMatch(matchDto3).getBody();
        long match4Id = mc.registerMatch(matchDto4).getBody();
        System.out.println("Registered Match ID: " + match1Id);
        System.out.println("Registered Match ID: " + match2Id);
        System.out.println("Registered Match ID: " + match3Id);
        System.out.println("Registered Match ID: " + match4Id);
        

        };
    }
}
