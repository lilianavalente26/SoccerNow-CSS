package pt.ul.fc.css.soccernow.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ul.fc.css.soccernow.dto.PlayerDto;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.enums.Position;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPlayer;

/**
 * Mapper class for converting between Player and PlayerDto objects.
 */
public class PlayerMapper {

	/**
	 * Converts entity Player to playerDto
	 * 
	 * @param player the entity Player
	 * @return dto
	 */
	public static PlayerDto toDto(Player player) {
		PlayerDto playerDto = new PlayerDto();
		playerDto.setName(player.getName());
		playerDto.setPreferedPosition(player.getPreferredPosition().toString());
		if (player.getTeams() != null) {
			playerDto.setTeams(new ArrayList<>(player.getTeamsIds()));
		}
		return playerDto;
	}
	
	/**
	 * Converts player to FormatedPlayerDto
	 * 
	 * @param player the entity Player
	 * @return dto
	 */
	public static ViewModelPlayer toViewModel(Player player) {
		ViewModelPlayer playerDto = new ViewModelPlayer();
		playerDto.setName(player.getName());
		playerDto.setPreferedPosition(player.getPreferredPosition().toString());
		if (player.getTeams() != null) {
			List<String> teamsClubNames = player.getTeams().stream().map(t -> t.getClub().getNameClub()).collect(Collectors.toList());
			playerDto.setTeams(teamsClubNames);
		}
		return playerDto;
	}
	
	/**
	 * Converts playerDto to entity player
	 * 
	 * @param dto the playarDto
	 * @param teams teams to add to player
	 * @return player entity
	 */
	public static Player toPlayer(PlayerDto dto, List<Team> teams) {
		Player player = new Player();
		player.setName(dto.getName());
		player.setPreferredPosition(Position.valueOf(dto.getPreferedPosition()));
		player.setTeams(teams);
		return player;
	}	
	
}
