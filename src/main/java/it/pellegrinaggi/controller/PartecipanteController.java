package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.model.Partecipante;
import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.StatoPellegrinaggio;

import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.repository.AdesioneRepository;
import it.pellegrinaggi.repository.PartecipanteRepository;
import it.pellegrinaggi.service.AdesioneService;
import it.pellegrinaggi.service.UtenteService;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/partecipante")
public class PartecipanteController {


    private final PellegrinaggioRepository pellegrinaggioRepository;

    private final AdesioneRepository adesioneRepository;
    
    private final PartecipanteRepository partecipanteRepository;

    private final UtenteService utenteService;
    
    private final AdesioneService adesioneService;



    public PartecipanteController(
            PellegrinaggioRepository pellegrinaggioRepository,
            AdesioneRepository adesioneRepository,
            PartecipanteRepository partecipanteRepository,
            UtenteService utenteService,
            AdesioneService adesioneService){


        this.pellegrinaggioRepository =
                pellegrinaggioRepository;


        this.adesioneRepository =
                adesioneRepository;
        
        this.partecipanteRepository =
        		partecipanteRepository;

        this.utenteService =
                utenteService;
       
        this.adesioneService =
        		adesioneService;
    }



    @GetMapping
    public String dashboard(
            Authentication authentication,
            Model model){



        Utente utente =
                utenteService
                .getUtenteLoggato(authentication);



        model.addAttribute(
                "partecipante",
                utente.getPartecipante()
        );



        model.addAttribute(
                "pellegrinaggi",
                pellegrinaggioRepository
                .findByStato(
                    StatoPellegrinaggio.APERTO
                )
        );



        model.addAttribute(
                "adesioni",
                adesioneRepository
                .findByUtenteId(
                    utente.getId()
                )
        );



        return "partecipante/dashboard";

    }
    
    @GetMapping("/familiare/nuovo/{pellegrinaggioId}")
    public String nuovoFamiliare(
            @PathVariable Integer pellegrinaggioId,
            Model model) {
    	
    	 Pellegrinaggio pellegrinaggio =
    	            pellegrinaggioRepository
    	            .findById(pellegrinaggioId)
    	            .orElseThrow();


        model.addAttribute(
            "partecipante",
            new Partecipante()
        );


        model.addAttribute(
                "pellegrinaggioId",
                pellegrinaggio.getId()
            );


            model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
            );

        return "partecipante/familiare-form";
    }
    
    @PostMapping("/familiare/salva")
    public String salvaFamiliare(
            @ModelAttribute Partecipante partecipante,
            Authentication authentication,
            @RequestParam Integer pellegrinaggioId
    ){

        Utente utente =
            utenteService.getUtenteLoggato(authentication);
        
        
        if(partecipanteRepository
                .existsByCodiceFiscaleAndIdNot(
                        partecipante.getCodiceFiscale(),
                        partecipante.getId()
                )) {

            throw new RuntimeException(
                "Codice fiscale già associato ad un altro partecipante"
            );
        }


        Partecipante salvato =
            partecipanteRepository.save(partecipante);


        Pellegrinaggio pellegrinaggio =
            pellegrinaggioRepository
            .findById(pellegrinaggioId)
            .orElseThrow();


        adesioneService.iscrivi(
            salvato,
            pellegrinaggio,
            utente
        );


        return "redirect:/partecipante";
    }


}