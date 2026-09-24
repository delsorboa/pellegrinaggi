package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.Adesione;
import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.Scelta;
import it.pellegrinaggi.model.Servizio;
import it.pellegrinaggi.model.Tabella;
import it.pellegrinaggi.model.ValoreTabella;
import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.repository.SceltaRepository;
import it.pellegrinaggi.repository.ServizioRepository;
import it.pellegrinaggi.repository.TabellaRepository;
import it.pellegrinaggi.service.ServizioService;
import it.pellegrinaggi.service.ValoreTabellaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(
        "/admin/pellegrinaggi/{pellegrinaggioId}/tabelle"
)
public class AdminPellegrinaggioTabellaController {

    private final TabellaRepository tabellaRepository;

    private final ServizioRepository servizioRepository;

    private final SceltaRepository sceltaRepository;

    private final PellegrinaggioRepository pellegrinaggioRepository;
    
    private final ServizioService servizioService;
    
    private final ValoreTabellaService valoreTabellaService;


    public AdminPellegrinaggioTabellaController(
            TabellaRepository tabellaRepository,
            ServizioRepository servizioRepository,
            SceltaRepository sceltaRepository,
            PellegrinaggioRepository pellegrinaggioRepository,
            ServizioService servizioService,
            ValoreTabellaService valoreTabellaService) {

        this.tabellaRepository = tabellaRepository;
        this.servizioRepository = servizioRepository;
        this.sceltaRepository = sceltaRepository;
        this.pellegrinaggioRepository = pellegrinaggioRepository;
        this.servizioService = servizioService;
        this.valoreTabellaService = valoreTabellaService;
    }


    // =========================================================
    // ELENCO TABELLE
    // =========================================================

    @GetMapping
    public String elencoTabelle(
            @PathVariable Integer pellegrinaggioId,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                )
                        );


        List<Tabella> tabelle =
                tabellaRepository.findAll();


        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "tabelle",
                tabelle
        );


        return "admin/tabelle/elenco";
    }


    // =========================================================
    // APERTURA TABELLA
    // =========================================================

    @GetMapping("/{tabellaId}")
    public String gestioneTabella(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer tabellaId,
            Model model) {


        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                )
                        );


        Tabella tabella =
                tabellaRepository
                        .findById(tabellaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Tabella non trovata"
                                )
                        );


        String tipo =
                tabella.getDescrizione()
                        .trim()
                        .toLowerCase();


        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "tabella",
                tabella
        );


        // =====================================================
        // SERVIZI
        // =====================================================

        if (tipo.equals("servizi")) {

            List<Servizio> servizi =
                    servizioRepository
                            .findByIdPellegrinaggio(
                                    pellegrinaggioId
                            );

            model.addAttribute(
                    "servizi",
                    servizi
            );

            return "admin/tabelle/servizi";
        }


        // =====================================================
        // SCELTA
        // =====================================================

        if (tipo.equals("scelta")) {

            List<Scelta> scelte =
                    sceltaRepository
                            .findAll(                                  
                            );

            model.addAttribute(
                    "scelte",
                    scelte
            );

            return "admin/tabelle/scelta";
        }


        throw new IllegalArgumentException(
                "Tipo di tabella non gestito: "
                        + tabella.getDescrizione()
        );
    }


    // =========================================================
    // NUOVO RECORD
    // =========================================================

    @GetMapping("/{tabellaId}/nuovo")
    public String nuovo(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer tabellaId,
            Model model) {


        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                )
                        );


        Tabella tabella =
                tabellaRepository
                        .findById(tabellaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Tabella non trovata"
                                )
                        );


        String tipo =
                tabella.getDescrizione()
                        .trim()
                        .toLowerCase();


        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "tabella",
                tabella
        );


        if (tipo.equals("servizi")) {

            model.addAttribute(
                    "servizio",
                    new Servizio()
            );

            return "admin/tabelle/servizio-form";
        }


        if (tipo.equals("scelta")) {

            model.addAttribute(
                    "scelta",
                    new Scelta()
            );

            return "admin/tabelle/scelta-form";
        }


        throw new IllegalArgumentException(
                "Tipo di tabella non gestito: "
                        + tabella.getDescrizione()
        );
    }


    // =========================================================
    // SALVA SERVIZIO
    // =========================================================

    @PostMapping("/{tabellaId}/servizi")
    public String salvaServizio(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer tabellaId,
            @RequestParam String descrizione) {


        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                )
                        );


        Servizio servizio =
                new Servizio();

        servizio.setDescrizione(
                descrizione.trim()
        );

        servizio.setIdPellegrinaggio(
                pellegrinaggio.getId()
        );


        servizioRepository.save(
                servizio
        );


        return "redirect:/admin/pellegrinaggi/"
        + pellegrinaggioId
        + "/tabelle/modifica/"
        + tabellaId;
    }
    
    @PostMapping("/{tabellaId}/servizi/{servizioId}/elimina")
    public String eliminaServizio(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer tabellaId,
            @PathVariable Integer servizioId) {

        servizioService.elimina(servizioId);

        return "redirect:/admin/pellegrinaggi/"
        + pellegrinaggioId
        + "/tabelle/modifica/"
        + tabellaId;
    }


    // =========================================================
    // SALVA SCELTA
    // =========================================================

    @PostMapping("/{tabellaId}/scelte")
    public String salvaScelta(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer tabellaId,
            @RequestParam String descrizione) {


        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                )
                        );


        Scelta scelta =
                new Scelta();

        scelta.setDescrizione(
                descrizione.trim()
        );

//        scelta.setPellegrinaggio(
//                pellegrinaggio
//        );


        sceltaRepository.save(
                scelta
        );


        return "redirect:/admin/pellegrinaggi/"
        + pellegrinaggioId
        + "/tabelle/modifica/"
        + tabellaId;
    }
    
    @PostMapping("/{tabellaId}/scelte/{sceltaId}/elimina")
    public String eliminaScelta(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer tabellaId,
            @PathVariable Integer sceltaId) {

    	sceltaRepository.deleteById(sceltaId);

        return "redirect:/admin/pellegrinaggi/"
        + pellegrinaggioId
        + "/tabelle/modifica/"
        + tabellaId;
    }
    

    @GetMapping("/modifica/{id}")
    public String modifica(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer id,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                )
                        );

        Tabella tabella =
                tabellaRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Tabella non trovata"
                                )
                        );

        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "tabella",
                tabella
        );


        // =====================================================
        // SERVIZI
        // =====================================================

        if ("servizi".equalsIgnoreCase(tabella.getDescrizione())) {

            List<Servizio> servizi =
                    servizioRepository
                            .findByIdPellegrinaggio(
                                    pellegrinaggioId
                            );

            model.addAttribute(
                    "servizi",
                    servizi
            );

            return "admin/tabelle/servizio-form";
        }


        // =====================================================
        // SCELTA
        // =====================================================

        if ("scelta".equalsIgnoreCase(tabella.getDescrizione())) {

            List<Scelta> scelte =
                    sceltaRepository
                            .findAll(                                
                            );

            model.addAttribute(
                    "scelte",
                    scelte
            );

            return "admin/tabelle/scelta-form";
        }
        
        if ("partecipante".equalsIgnoreCase(tabella.getDescrizione())) {

            return "redirect:/admin/partecipanti";
        }


        throw new IllegalArgumentException(
                "Tipo di tabella non gestito: "
                        + tabella.getDescrizione()
        );
    }
    
   
 


    }



