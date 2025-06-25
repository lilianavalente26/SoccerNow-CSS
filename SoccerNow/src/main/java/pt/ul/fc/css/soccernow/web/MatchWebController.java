package pt.ul.fc.css.soccernow.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.handlers.MatchHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelMatch;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MatchWebController {

    private final MatchHandler matchHandler;
    
    private List<ViewModelMatch> lastMatchResults = new ArrayList<>();

    @Autowired
    public MatchWebController(MatchHandler matchHandler) {
    	this.matchHandler = matchHandler;
    }
    
    @GetMapping("/matches")
    public String showAllMathes(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) { return "redirect:/login"; }
    	List<ViewModelMatch> matches = matchHandler.handleGetAllMatches();
    	model.addAttribute("matches", matches);
    	lastMatchResults = matches;
    	return "matches";
    }
    
    @GetMapping("/matches/filter-by/result")
    public String filterMatches(
            @RequestParam(required = false) Boolean isOver,
            @RequestParam(required = false) Integer minGoals,
            @RequestParam(required = false) Integer maxGoals,
            @RequestParam(required = false) String stadiumName,
            @RequestParam(required = false) String matchPeriod,
            Model model,
            HttpSession session
    ) {
        String username = (String) session.getAttribute("username");
        if (username == null) { return "redirect:/login"; }

    	if (matchPeriod.equals("")) {
    		matchPeriod = null;
    	}
    	if (stadiumName.equals("")) {
    		stadiumName = null;
    	}
        List<ViewModelMatch> matches = matchHandler.handleSearchFilterMatch(isOver, minGoals, maxGoals, stadiumName, matchPeriod);
        model.addAttribute("matches", matches);
        lastMatchResults = matches;
        return "matches"; 
    }
    
    @GetMapping("/matches/details")
    public String showMatchDetails(Long matchId, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) { return "redirect:/login"; }
    	ViewModelMatch match = matchHandler.getFormatedMatch(matchId);
    	model.addAttribute("match", match);
    	return "matchDetails";
    }
    
}
