package pt.ul.fc.css.soccernow.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import pt.ul.fc.css.soccernow.handlers.TournamentHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPointsTournament;

@Controller
public class TournamentWebController {

	private final TournamentHandler tournamentHandler;
	
	@Autowired
	public TournamentWebController(TournamentHandler tournamentHandler) {
		this.tournamentHandler = tournamentHandler;
	}
	
	@GetMapping("/tournaments")
	public String showAllTournaments(Model model, HttpSession session) {
		String username = (String) session.getAttribute("username");
		if (username == null) {return "redirect:/login";}
		List<ViewModelPointsTournament> tournaments = tournamentHandler.handleGetAllPointsTournaments();
		model.addAttribute("tournaments", tournaments);
		return "tournaments";
	}
	
	@GetMapping("/tournaments/filter-by/result")
	public String showFilterTournaments(
			@RequestParam(required = false) String name,
    		@RequestParam(required = false) String clubName,
    		@RequestParam(required = false) Integer minRealizedMatches,
    		@RequestParam(required = false) Integer maxRealizedMatches,
    		@RequestParam(required = false) Integer minToDoMatches,
    		@RequestParam(required = false) Integer maxToDoMatches,
			Model model, HttpSession session) {
		
		String username = (String) session.getAttribute("username");
		if (username == null) {return "redirect:/login";}
		
		if (name.equals("")) {
			name = null;
		}
		if (clubName.equals("")) {
			clubName = null;
		}
		
		List<ViewModelPointsTournament> tournaments = tournamentHandler.filterByTournament(name, clubName, minRealizedMatches, maxRealizedMatches, minToDoMatches, maxToDoMatches);
		model.addAttribute("tournaments", tournaments);
		return "tournaments";
	}
	
}
