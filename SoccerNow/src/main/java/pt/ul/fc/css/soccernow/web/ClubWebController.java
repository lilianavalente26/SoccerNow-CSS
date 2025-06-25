package pt.ul.fc.css.soccernow.web;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ul.fc.css.soccernow.handlers.ClubHandler;
import pt.ul.fc.css.soccernow.handlers.TeamHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelClub;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelTeam;

@Controller
public class ClubWebController {

	private final ClubHandler clubHandler;
	private final TeamHandler teamHandler;
	
	List<ViewModelClub> lastClubs = new ArrayList<>();
	
	@Autowired
	public ClubWebController(ClubHandler clubHandler, TeamHandler teamHandler) {
		this.clubHandler = clubHandler;
		this.teamHandler = teamHandler;
	}
	
	@GetMapping("/clubs")
	public String showAllClubs(Model model, HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) { return "redirect:/login"; }
		List<ViewModelClub> clubs = clubHandler.handleGetAllClubs();
		lastClubs = clubs;
		model.addAttribute("clubs", clubs);
		return "clubs";
	}
	
	@GetMapping("/clubs/filter-by/result")
	public String filterClub(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer minPlayers,
			@RequestParam(required = false) Integer maxPlayers,
			@RequestParam(required = false) Integer nWins,
			@RequestParam(required = false) Integer nDraws,
			@RequestParam(required = false) Integer nLosses,
			@RequestParam(required = false) Integer nAchievements,
			@RequestParam(required = false) Integer achievementPosition,
			@RequestParam(required = false) String missingPlayerPosition,
			Model model,
			HttpSession session) {

		String username = (String) session.getAttribute("username");
		if (username == null) { return "redirect:/login"; }

		if (name.equals("")) {
			name = null;
		}
		if (missingPlayerPosition.equals("")) {
			missingPlayerPosition = null;
		}
		
		List<ViewModelClub> clubs = clubHandler.handleSearchFilterClub(name, minPlayers, maxPlayers, nWins, nDraws, nLosses, nAchievements, achievementPosition, missingPlayerPosition);
		lastClubs = clubs;
		model.addAttribute("clubs", clubs);
		return "clubs";
	}
	
	@GetMapping("/clubs/teams")
	public String getTeamsFromClub(Long clubId, Model model, HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) { return "redirect:/login"; }
		List<ViewModelTeam> teams = teamHandler.handleGetAllTeamsOfClub(clubId);
		model.addAttribute("teams", teams);
		return "teams";
	}
	
}
