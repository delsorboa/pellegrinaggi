package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.Sistemazione;
import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.repository.SistemazioneRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pellegrinaggi")
public class AdminSistemazioneController {

    private final PellegrinaggioRepository pellegrinaggioRepository;
    private final SistemazioneRepository sistemazioneRepository;


    public AdminSistemazioneController(
            PellegrinaggioRepository pellegrinaggioRepository,
            SistemazioneRepository sistemazioneRepository) {

        this.pellegrinaggioRepository = pellegrinaggioRepository;
        this.sistemazioneRepository = sistemazioneRepository;
    }


    /**
     * ELENCO SISTEMAZIONI
     *
     * URL:
     * /admin/pellegrinaggi/{pellegrinaggioId}/sistemazioni
     */
    @GetMapping("/{pellegrinaggioId}/sistemazioni")
    public String elenco(
            @PathVariable Integer pellegrinaggioId,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository.findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato: "
                                                + pellegrinaggioId));

        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "sistemazioni",
                sistemazioneRepository
                        .findByIdPellegrinaggio(pellegrinaggioId)
        );

        return "admin/sistemazioni/elenco";
    }


    /**
     * NUOVA SISTEMAZIONE
     *
     * URL:
     * /admin/pellegrinaggi/{pellegrinaggioId}/sistemazioni/nuovo
     */
    @GetMapping("/{pellegrinaggioId}/sistemazioni/nuovo")
    public String nuovo(
            @PathVariable Integer pellegrinaggioId,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository.findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato: "
                                                + pellegrinaggioId));

        Sistemazione sistemazione = new Sistemazione();

        /*
         * Impostiamo subito il pellegrinaggio.
         */
        sistemazione.setIdPellegrinaggio(pellegrinaggioId);

        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "sistemazione",
                sistemazione
        );

        return "admin/sistemazioni/form";
    }


    /**
     * MODIFICA SISTEMAZIONE
     *
     * URL:
     * /admin/pellegrinaggi/{pellegrinaggioId}/sistemazioni/modifica/{id}
     */
    @GetMapping("/{pellegrinaggioId}/sistemazioni/modifica/{id}")
    public String modifica(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer id,
            Model model) {

        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository.findById(pellegrinaggioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pellegrinaggio non trovato: "
                                                + pellegrinaggioId));

        Sistemazione sistemazione =
                sistemazioneRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sistemazione non trovata: "
                                                + id));

        /*
         * Controlliamo che la sistemazione
         * appartenga realmente al pellegrinaggio.
         */
        if (!pellegrinaggioId.equals(
                sistemazione.getIdPellegrinaggio())) {

            throw new IllegalArgumentException(
                    "La sistemazione non appartiene "
                            + "al pellegrinaggio");
        }

        model.addAttribute(
                "pellegrinaggio",
                pellegrinaggio
        );

        model.addAttribute(
                "sistemazione",
                sistemazione
        );

        return "admin/sistemazioni/form";
    }


    /**
     * SALVATAGGIO
     *
     * URL:
     * /admin/pellegrinaggi/sistemazioni/salva
     */
    @PostMapping("/sistemazioni/salva")
    public String salva(
            @ModelAttribute Sistemazione sistemazione,
            @RequestParam("pellegrinaggioId")
            Integer pellegrinaggioId) {

        /*
         * Verifichiamo che il pellegrinaggio esista.
         */
        pellegrinaggioRepository.findById(pellegrinaggioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pellegrinaggio non trovato: "
                                        + pellegrinaggioId));

        /*
         * Impostiamo sempre il pellegrinaggio
         * ricevuto dall'URL.
         *
         * In questo modo non ci affidiamo
         * al valore eventualmente manipolato
         * dal form.
         */
        sistemazione.setIdPellegrinaggio(
                pellegrinaggioId
        );

        sistemazioneRepository.save(sistemazione);

        return "redirect:/admin/pellegrinaggi/"
                + pellegrinaggioId
                + "/sistemazioni";
    }


    /**
     * ELIMINA SISTEMAZIONE
     *
     * URL:
     * /admin/pellegrinaggi/{pellegrinaggioId}/sistemazioni/elimina/{id}
     */
    @GetMapping("/{pellegrinaggioId}/sistemazioni/elimina/{id}")
    public String elimina(
            @PathVariable Integer pellegrinaggioId,
            @PathVariable Integer id) {

        Sistemazione sistemazione =
                sistemazioneRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sistemazione non trovata: "
                                                + id));

        /*
         * Controlliamo che la sistemazione
         * appartenga al pellegrinaggio.
         */
        if (!pellegrinaggioId.equals(
                sistemazione.getIdPellegrinaggio())) {

            throw new IllegalArgumentException(
                    "La sistemazione non appartiene "
                            + "al pellegrinaggio");
        }

        sistemazioneRepository.delete(sistemazione);

        return "redirect:/admin/pellegrinaggi/"
                + pellegrinaggioId
                + "/sistemazioni";
    }
}
