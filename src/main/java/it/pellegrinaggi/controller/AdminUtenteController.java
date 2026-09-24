package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


@Controller
@RequestMapping("/admin/utenti")
public class AdminUtenteController {


    private final UtenteRepository repository;
    private final PasswordEncoder passwordEncoder;


    public AdminUtenteController(UtenteRepository repository,
            PasswordEncoder passwordEncoder) {
    		this.repository = repository;
			this.passwordEncoder = passwordEncoder;
    }


    @GetMapping
    public String elenco(Model model){


        model.addAttribute(
                "utenti",
                repository.findAll()
        );


        return "admin/utenti/elenco";

    }



    @GetMapping("/abilita/{id}")
    public String abilita(
            @PathVariable Integer id){


        Utente utente =
                repository.findById(id)
                .orElseThrow();


        utente.setAttivo(true);


        repository.save(utente);


        return "redirect:/admin/utenti";

    }



    @GetMapping("/disabilita/{id}")
    public String disabilita(
            @PathVariable Integer id){


        Utente utente =
                repository.findById(id)
                .orElseThrow();


        utente.setAttivo(false);


        repository.save(utente);


        return "redirect:/admin/utenti";

    }




    @GetMapping("/modifica-password/{id}")
    public String modificaPassword(@PathVariable Integer id, Model model) {

        Optional<Utente> optionalUtente = repository.findById(id);

        if (optionalUtente.isEmpty()) {
            return "redirect:/admin/utenti";
        }

        Utente utente = optionalUtente.get();

        model.addAttribute("utente", utente);

        return "admin/utenti/modifica-password";
    }
    
    
    @PostMapping("/modifica-password/{id}")
    public String salvaNuovaPassword(
            @PathVariable Integer id,
            @RequestParam String password,
            @RequestParam String confermaPassword,
            Model model) {

        Utente utente = repository.findById(id).orElse(null);

        if (utente == null) {
            return "redirect:/admin/utenti";
        }

        // Controllo password vuota
        if (password == null || password.isBlank()) {

            model.addAttribute("utente", utente);
            model.addAttribute("errore", "La password non può essere vuota.");

            return "admin/modifica-password";
        }

        // Controllo corrispondenza password
        if (!password.equals(confermaPassword)) {

            model.addAttribute("utente", utente);
            model.addAttribute("errore", "Le password non coincidono.");

            return "admin/modifica-password";
        }

        // Criptazione BCrypt
        String passwordCriptata = passwordEncoder.encode(password);

        // Salvataggio nel campo password
        utente.setPassword(passwordCriptata);

        repository.save(utente);

        return "redirect:/admin/utenti";
    }

}
