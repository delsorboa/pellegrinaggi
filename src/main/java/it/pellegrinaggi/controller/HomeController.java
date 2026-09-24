package it.pellegrinaggi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


@GetMapping("/home")
public String home(
Authentication authentication){

    if(authentication
        .getAuthorities()
        .stream()
        .anyMatch(
          a -> a.getAuthority()
          .equals("ROLE_ADMIN")
        )){


        return "redirect:/admin/dashboard";

    }


    return "redirect:/utente/home";

}

}