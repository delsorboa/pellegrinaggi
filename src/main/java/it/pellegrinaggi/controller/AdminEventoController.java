package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.Eventi;
import it.pellegrinaggi.model.Pellegrinaggio;

import it.pellegrinaggi.repository.EventoRepository;
import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.repository.TabellaRepository;
import it.pellegrinaggi.repository.TipoEventoRepository;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pellegrinaggi")
public class AdminEventoController {

    private final EventoRepository eventoRepository;

    private final PellegrinaggioRepository pellegrinaggioRepository;

    private final TipoEventoRepository tipoEventoRepository;
    
    private final TabellaRepository tabellaRepository;



    public AdminEventoController(
            EventoRepository eventoRepository,
            PellegrinaggioRepository pellegrinaggioRepository,
            TipoEventoRepository tipoEventoRepository,
            TabellaRepository tabellaRepository) {

        this.eventoRepository = eventoRepository;

        this.pellegrinaggioRepository =
                pellegrinaggioRepository;

        this.tipoEventoRepository =
                tipoEventoRepository;

        this.tabellaRepository =
                tabellaRepository;
    }



    /**
     * ELENCO EVENTI
     */
    @GetMapping("/{idPellegrinaggio}/eventi")
    public String elenco(
            @PathVariable Integer idPellegrinaggio,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(idPellegrinaggio)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                ));


        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );


        model.addAttribute(
                "eventi",
                eventoRepository
                        .findByIdPellegrinaggio(
                                idPellegrinaggio
                        )
        );


        model.addAttribute(
                "tipiEvento",
                tipoEventoRepository.findAll()
        );
        
        model.addAttribute(
                "tabelle",
                tabellaRepository.findAll()
        );


        return "admin/eventi/elenco";
    }


    /**
     * NUOVO EVENTO
     */
    @GetMapping("/{idPellegrinaggio}/eventi/nuovo")
    public String nuovo(
            @PathVariable Integer idPellegrinaggio,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(idPellegrinaggio)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                ));


        Eventi evento = new Eventi();

        evento.setIdPellegrinaggio(
                idPellegrinaggio
        );


        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );


        model.addAttribute(
                "evento",
                evento
        );
        
        model.addAttribute(
                "tabelle",
                tabellaRepository.findAll()
        );



        model.addAttribute(
                "tipiEvento",
                tipoEventoRepository.findAll()
        );


        return "admin/eventi/form";
    }


    /**
     * MODIFICA EVENTO
     */
    @GetMapping("/{idPellegrinaggio}/eventi/modifica/{id}")
    public String modifica(
            @PathVariable Integer idPellegrinaggio,
            @PathVariable Integer id,
            Model model) {

        Eventi evento =
                eventoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evento non trovato"
                                ));


        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                        .findById(idPellegrinaggio)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato"
                                ));


        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );


        model.addAttribute(
                "evento",
                evento
        );


        model.addAttribute(
                "tipiEvento",
                tipoEventoRepository.findAll()
        );


        return "admin/eventi/form";
    }


    /**
     * SALVA EVENTO
     */
    @PostMapping("/{idPellegrinaggio}/eventi/salva")
    public String salva(
            @PathVariable Integer idPellegrinaggio,
            @ModelAttribute Eventi evento) {

        // Il pellegrinaggio viene sempre
        // impostato dal percorso URL
        evento.setIdPellegrinaggio(
                idPellegrinaggio
        );


        eventoRepository.save(evento);


        return "redirect:/admin/pellegrinaggi/"
                + idPellegrinaggio
                + "/eventi";
    }


    /**
     * ELIMINA EVENTO
     */
    @GetMapping("/{idPellegrinaggio}/eventi/elimina/{id}")
    public String elimina(
            @PathVariable Integer idPellegrinaggio,
            @PathVariable Integer id) {

        eventoRepository.deleteById(id);


        return "redirect:/admin/pellegrinaggi/"
                + idPellegrinaggio
                + "/eventi";
    }
    
  
}

