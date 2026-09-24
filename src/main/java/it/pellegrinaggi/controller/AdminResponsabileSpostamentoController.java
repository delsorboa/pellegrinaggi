package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.ResponsabileSpostamento;
import it.pellegrinaggi.model.ResponsabileSpostamentoId;
import it.pellegrinaggi.model.Spostamento;
import it.pellegrinaggi.model.Partecipante;

import it.pellegrinaggi.repository.ResponsabileSpostamentoRepository;
import it.pellegrinaggi.repository.SpostamentoRepository;
import it.pellegrinaggi.repository.PartecipanteRepository;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/spostamenti")
public class AdminResponsabileSpostamentoController {

    private final SpostamentoRepository spostamentoRepository;
    private final ResponsabileSpostamentoRepository
            responsabileSpostamentoRepository;
    private final PartecipanteRepository partecipanteRepository;


    public AdminResponsabileSpostamentoController(
            SpostamentoRepository spostamentoRepository,
            ResponsabileSpostamentoRepository
                    responsabileSpostamentoRepository,
            PartecipanteRepository partecipanteRepository) {

        this.spostamentoRepository = spostamentoRepository;
        this.responsabileSpostamentoRepository =
                responsabileSpostamentoRepository;
        this.partecipanteRepository =
                partecipanteRepository;
    }


    /**
     * ELENCO RESPONSABILI
     */
    @GetMapping("/{idSpostamento}/responsabili")
    public String elenco(
            @PathVariable Integer idSpostamento,
            Model model) {

        Spostamento spostamento =
                spostamentoRepository.findById(idSpostamento)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Spostamento non trovato"));

        var responsabili =
                responsabileSpostamentoRepository
                        .findByIdIdSpostamento(idSpostamento);

        Map<Integer, Partecipante> partecipantiMap =
                partecipanteRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                Partecipante::getId,
                                p -> p
                        ));

        model.addAttribute(
                "partecipantiMap",
                partecipantiMap
        );

        model.addAttribute(
                "spostamento",
                spostamento
        );

        model.addAttribute(
                "responsabili",
                responsabili
        );


        return "admin/responsabili-spostamento/elenco";
    }



    /**
     * FORM AGGIUNTA RESPONSABILE
     */
    @GetMapping("/{idSpostamento}/responsabili/nuovo")
    public String nuovo(
            @PathVariable Integer idSpostamento,
            Model model) {

        Spostamento spostamento =
                spostamentoRepository.findById(idSpostamento)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Spostamento non trovato"));

        model.addAttribute(
                "spostamento",
                spostamento
        );

        model.addAttribute(
                "partecipanti",
                partecipanteRepository.findAll()
        );

        return "admin/responsabili-spostamento/form";
    }


    /**
     * SALVA RESPONSABILE
     */
    @PostMapping("/{idSpostamento}/responsabili/salva")
    public String salva(
            @PathVariable Integer idSpostamento,
            @RequestParam Integer idPartecipante) {

        Spostamento spostamento =
                spostamentoRepository.findById(idSpostamento)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Spostamento non trovato"));

        Partecipante partecipante =
                partecipanteRepository.findById(idPartecipante)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Partecipante non trovato"));

        ResponsabileSpostamento responsabile =
                new ResponsabileSpostamento(
                        spostamento.getId(),
                        partecipante.getId()
                );

        responsabileSpostamentoRepository.save(
                responsabile
        );

        return "redirect:/admin/spostamenti/"
                + idSpostamento
                + "/responsabili";
    }


    /**
     * ELIMINA RESPONSABILE
     */
    @GetMapping(
            "/{idSpostamento}/responsabili/elimina/{idPartecipante}")
    public String elimina(
            @PathVariable Integer idSpostamento,
            @PathVariable Integer idPartecipante) {

        ResponsabileSpostamentoId id =
                new ResponsabileSpostamentoId();

        id.setIdSpostamento(idSpostamento);
        id.setIdPartecipante(idPartecipante);

        if (responsabileSpostamentoRepository
                .existsById(id)) {

            responsabileSpostamentoRepository.deleteById(id);
        }

        return "redirect:/admin/spostamenti/"
                + idSpostamento
                + "/responsabili";
    }
}
