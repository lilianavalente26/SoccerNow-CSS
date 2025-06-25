package pt.ul.fc.css.soccernow.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class WebController {

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	// Denis, aqui este model é usado para passar a estutura de dados (aka String) do controller para a view
	// Session guarda as infos do utilizador durante a sessão específica
	@PostMapping("/login")
	public String processLogin(@RequestParam String username,
							   @RequestParam String password,
							   HttpSession session,
							   Model model) {
		if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
			session.setAttribute("username", username);
			return "redirect:/";
		}

		model.addAttribute("error", "Username e Password são obrigatórios.");
		return "login";
	}

	@GetMapping("/")
	public String showHomePage(HttpSession session, Model model) {
		String username = (String) session.getAttribute("username");
		if (username == null) { return "redirect:/login"; }
		model.addAttribute("username", username);
		return "home";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
	
}
