package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.Spostamento;
import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.repository.SpostamentoRepository;
import it.pellegrinaggi.repository.TipMezzoRepository;
import it.pellegrinaggi.repository.TipViaggioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pellegrinaggi")
public class AdminSpostamentoController {

    private final PellegrinaggioRepository pellegrinaggioRepository;
    private final SpostamentoRepository spostamentoRepository;
    private final TipMezzoRepository tipMezzoRepository;
    private final TipViaggioRepository tipViaggioRepository;

    public AdminSpostamentoController(
            PellegrinaggioRepository pellegrinaggioRepository,
            SpostamentoRepository spostamentoRepository,
            TipMezzoRepository tipMezzoRepository,
            TipViaggioRepository tipViaggioRepository) {

        this.pellegrinaggioRepository = pellegrinaggioRepository;
        this.spostamentoRepository = spostamentoRepository;
        this.tipMezzoRepository = tipMezzoRepository;
        this.tipViaggioRepository = tipViaggioRepository;
    }


    /**
     * Elenco degli spostamenti del pellegrinaggio
     */
    @GetMapping("/{pellegrinaggioId}/spostamenti")
    public String elenco(
            @PathVariable Integer pellegrinaggioId,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository.findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Pellegrinaggio non trovato"));

        model.addAttribute("pellegrinaggio", pellegrinaggio);

        model.addAttribute(
                "spostamenti",
                spostamentoRepository.findByPellegrinaggio_id(pellegrinaggioId)
        );

        return "admin/spostamenti/elenco";
    }



    /**
     * Form nuovo spostamento
     */
    @GetMapping("/{pellegrinaggioId}/spostamenti/nuovo")
    public String nuovo(
            @PathVariable Integer pellegrinaggioId,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository.findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Pellegrinaggio non trovato"));

        Spostamento spostamento = new Spostamento();

        spostamento.setPellegrinaggio(pellegrinaggio);

        model.addAttribute("pellegrinaggio", pellegrinaggio);
        model.addAttribute("spostamento", spostamento);

        model.addAttribute("tipMezzi",
                tipMezzoRepository.findAll());

        model.addAttribute("tipViaggi",
                tipViaggioRepository.findAll());

        return "admin/spostamenti/form";
    }


    /**
     * Form modifica spostamento
     */
    @GetMapping("/{pellegrinaggioId}/spostamenti/modifica/{id}")
    public String modifica(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer id,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository.findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Pellegrinaggio non trovato"));

        Spostamento spostamento =
                spostamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Spostamento non trovato"));

        // Controlliamo che lo spostamento appartenga
        // realmente al pellegrinaggio selezionato
        if (!spostamento.getPellegrinaggio().getId().equals(pellegrinaggioId)) {
            throw new IllegalArgumentException(
                    "Lo spostamento non appartiene al pellegrinaggio"
            );
        }

        model.addAttribute("pellegrinaggio", pellegrinaggio);
        model.addAttribute("spostamento", spostamento);

        model.addAttribute("tipMezzi",
                tipMezzoRepository.findAll());

        model.addAttribute("tipViaggi",
                tipViaggioRepository.findAll());

        return "admin/spostamenti/form";
    }


    /**
     * Salvataggio
     */
    @PostMapping("/spostamenti/salva")
    public String salva(
            @ModelAttribute Spostamento spostamento) {

        spostamentoRepository.save(spostamento);

        return "redirect:/admin/pellegrinaggi/"
                + spostamento.getPellegrinaggio().getId()
                + "/spostamenti";
    }


    /**
     * Eliminazione
     */
    @GetMapping("/{pellegrinaggioId}/spostamenti/elimina/{id}")
    public String elimina(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer id) {

        Spostamento spostamento =
                spostamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Spostamento non trovato"));

        // Evitiamo di eliminare uno spostamento
        // appartenente ad un altro pellegrinaggio
        if (!spostamento.getPellegrinaggio().getId().equals(pellegrinaggioId)) {
            throw new IllegalArgumentException(
                    "Lo spostamento non appartiene al pellegrinaggio"
            );
        }

        spostamentoRepository.delete(spostamento);

        return "redirect:/admin/pellegrinaggi/"
                + pellegrinaggioId
                + "/spostamenti";
    }
}

