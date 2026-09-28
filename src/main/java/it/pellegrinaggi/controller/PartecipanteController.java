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



        // 2. Estrae gli ID dei pellegrinaggi a cui il partecipante ha già aderito
        java.util.Set<Integer> idPellegrinaggiIscritto =   adesioneRepository
                .findByPartecipante(
                		utente.getPartecipante()
                ).stream()
                .filter(a -> a.getPellegrinaggio() != null)
                .map(a -> a.getPellegrinaggio().getId())
                .collect(java.util.stream.Collectors.toSet());

        // 3. Recupera i pellegrinaggi aperti e filtra escludendo quelli a cui è già iscritto
        java.util.List<Pellegrinaggio> pellegrinaggiDisponibili = 
                pellegrinaggioRepository.findByStato(StatoPellegrinaggio.APERTO)
                .stream()
                .filter(p -> !idPellegrinaggiIscritto.contains(p.getId()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute(
                "pellegrinaggi",
                pellegrinaggiDisponibili
        );



        model.addAttribute(
                "adesioni",
                adesioneRepository
                .findByPartecipante(
                		utente.getPartecipante()
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
    
    @PostMapping("/salva")
    public String salvaAdesioneDiretta(
            @RequestParam("pellegrinaggioId") Integer pellegrinaggioId,
            Authentication authentication) {

        // 1. Recupera l'utente correntemente loggato a sistema
        Utente utente = utenteService.getUtenteLoggato(authentication);
        
        // 2. Recupera l'anagrafica partecipante associata all'account utente
        Partecipante partecipante = utente.getPartecipante();
        
        if (partecipante == null) {
            throw new RuntimeException("L'utente loggato non è collegato ad alcuna anagrafica partecipante.");
        }

        // 3. Cerca il pellegrinaggio selezionato
        Pellegrinaggio pellegrinaggio = pellegrinaggioRepository.findById(pellegrinaggioId).orElseThrow();

        // 4. Registra l'iscrizione sul database tramite il Service dedicato
        adesioneService.iscrivi(
            partecipante,
            pellegrinaggio,
            utente
        );

        // 5. Ridirige in sicurezza alla dashboard principale, scongiurando l'errore di template mancante
        return "redirect:/partecipante";
    }


}