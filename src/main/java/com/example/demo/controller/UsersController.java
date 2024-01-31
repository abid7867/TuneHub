package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.LoginData;
import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.service.SongService;
import com.example.demo.service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {
	@Autowired
	UsersService service;
	
	@Autowired
	SongService songService;

	@PostMapping("/register")
	public String addUsers(@ModelAttribute Users user) {
		boolean userStatus = service.emailExists(user.getEmail());
		if(userStatus  == false) {
			service.addUser(user);
			System.out.println("user added");
		}
		else {
			System.out.println("user already exists");
		}


		return "index";
	}

	@PostMapping("/validate")
	public String validate(@RequestParam("email") String email,
			@RequestParam("password") String password,
			HttpSession session, Model model) {

		if(service.validateUser(email,password) == true) {
			String role = service.getRole(email);

			session.setAttribute("email", email);

			if(role.equals("admin")) {
				return "adminHome";
			}
			else {
				Users user = service.getUser(email);
				
				boolean userStatus = user.isPremium();
				String userName = user.getUsername();
				//System.out.println(userStatus);
				List<Song> songList = songService.fetchAllSongs();
				model.addAttribute("songs", songList);
				model.addAttribute("isPremium", userStatus);
				model.addAttribute("userName", userName);
				
				return "customerHome";
			}
		}
		else {
			return "login";
		}
	}



	@GetMapping("/logout")
	public String logout(HttpSession session) {

		session.invalidate();
		return "login";
	}
	
	
	@GetMapping("/showUsers")
	public String showUsers(Model model) {
		List<Users> userList = service.findAll();
		model.addAttribute("userList", userList);
		return "displayUsers";
	}



}