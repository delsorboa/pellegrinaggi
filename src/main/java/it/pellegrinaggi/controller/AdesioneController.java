package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.service.AdesioneService;
import it.pellegrinaggi.service.UtenteService;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/partecipante/adesione")
public class AdesioneController {


    private final PellegrinaggioRepository pellegrinaggioRepository;

    private final AdesioneService adesioneService;

    private final UtenteService utenteService;



    public AdesioneController(
            PellegrinaggioRepository pellegrinaggioRepository,
            AdesioneService adesioneService,
            UtenteService utenteService){


        this.pellegrinaggioRepository =
                pellegrinaggioRepository;


        this.adesioneService =
                adesioneService;


        this.utenteService =
                utenteService;

    }





    @GetMapping("/{id}")
    public String conferma(
            @PathVariable Integer id){


        return "partecipante/conferma-adesione";

    }






    @PostMapping("/{id}")
    public String salva(
            @PathVariable Integer id,
            Authentication authentication){



        Utente utente =
                utenteService
                .getUtenteLoggato(authentication);



        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                .findById(id)
                .orElseThrow();



        adesioneService.iscrivi(
                utente.getPartecipante(),
                pellegrinaggio,
                utente
        );



        return "redirect:/partecipante";

    }


}