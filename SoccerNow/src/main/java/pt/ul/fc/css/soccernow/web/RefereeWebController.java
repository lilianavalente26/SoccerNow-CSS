package pt.ul.fc.css.soccernow.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ul.fc.css.soccernow.handlers.UserHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelReferee;

import java.util.List;

@Controller
public class RefereeWebController {

    private final UserHandler userHandler;

    @Autowired
    public RefereeWebController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }
    
    @GetMapping("/referees")
    public String showAllReferees(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) { return "redirect:/login"; }
    	List<ViewModelReferee> referees = userHandler.handleFindAllReferees();
    	model.addAttribute("referees", referees);
    	return "referees";
    }

    @GetMapping("/referees/filter-by/result")
    public String filterReferee(
            @RequestParam String type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            Model model,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) { return "redirect:/login"; }

        List<ViewModelReferee> referees;

        switch (type) {
            case "name":
            	if (!name.equals("")) {
                    referees = userHandler.handleFilterRefereesByName(name);
            	} else {
            		referees = List.of();
            	}
                break;
            case "matches-oficialized":
                referees = userHandler.handleFilterRefereesByMatchesOficialized(max, min);
                break;
            case "cards-shown":
                referees = userHandler.handleFilterRefereesByCardsShown(max, min);
                break;
            default:
                referees = List.of();
        }

        model.addAttribute("referees", referees);
        return "referees";
    }
}
