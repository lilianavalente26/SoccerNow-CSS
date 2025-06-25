package pt.ul.fc.css.soccernow.web;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ul.fc.css.soccernow.handlers.UserHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPlayer;

@Controller
public class PlayerWebController {

	private final UserHandler userHandler;
	
	@Autowired
	public PlayerWebController(UserHandler userHandler) {
		this.userHandler = userHandler;
	}
		
    @GetMapping("/players")
    public String showAllPlayers(Model model, HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) { return "redirect:/login"; }
    	List<ViewModelPlayer> players = userHandler.handleFindAllPlayers();
    	model.addAttribute("players", players);
    	return "players";
    }
	
	@GetMapping("/players/filter-by/result")
	public String filterPlayer(
			@RequestParam String type,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String position,
			@RequestParam(required = false) Integer min,
			@RequestParam(required = false) Integer max,
			Model model,
			HttpSession session) {

		// linhas mágicas para verificar se o utilizador está autenticado
		String username = (String) session.getAttribute("username");
		if (username == null) { return "redirect:/login"; }

		List<ViewModelPlayer> players = new ArrayList<>();

		switch (type) {
			case "name":
				if (!name.isEmpty()) { players = userHandler.handleFilterPlayersByName(name);}
				else { players = new ArrayList<>();}
				break;
			case "position":
				players = userHandler.handleFilterPlayersByPosition(position);
				break;
			case "goals":
				players = userHandler.handleFilterPlayersByGoalsScored(max, min);
				break;
			case "cards":
				players = userHandler.handleFilterPlayersByCardsReceived(max, min);
				break;
			case "games":
				players = userHandler.handleFilterPlayersByGamesPlayed(max, min);
				break;
			default:
				players = List.of();
		}

		model.addAttribute("players", players);
		return "players";
	}
}
