package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;
import it.pellegrinaggi.service.UtenteService;


import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
public class CambioPasswordController {


    private final UtenteService utenteService;

    private final UtenteRepository repository;

    private final PasswordEncoder encoder;



    public CambioPasswordController(
            UtenteService utenteService,
            UtenteRepository repository,
            PasswordEncoder encoder){


        this.utenteService = utenteService;
        this.repository = repository;
        this.encoder = encoder;

    }



    @GetMapping("/cambio-password")
    public String pagina(){

        return "cambio-password";

    }




    @PostMapping("/cambio-password")
    public String cambia(
            Authentication authentication,
            @RequestParam String nuovaPassword,
            @RequestParam String confermaPassword,
            Model model){



        if(!nuovaPassword.equals(confermaPassword)){


            model.addAttribute(
                    "errore",
                    "Le password non coincidono"
            );


            return "cambio-password";

        }



        Utente utente =
                utenteService
                .getUtenteLoggato(authentication);



        utente.setPassword(
                encoder.encode(nuovaPassword)
        );


        utente.setCambioPasswordObbligatorio(false);



        repository.save(utente);



        if(utente.getRuolo().name().equals("ADMIN")){

            return "redirect:/admin";

        }


        return "redirect:/partecipante";

    }

}
