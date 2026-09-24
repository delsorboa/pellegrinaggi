package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.*;
import it.pellegrinaggi.service.PartecipanteSistemazioneService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/adesioni/{adesioneId}/sistemazioni")
public class AdminPartecipanteSistemazioneController {

    private final AdesioneRepository adesioneRepository;
    private final SistemazioneRepository sistemazioneRepository;
    private final CameraRepository cameraRepository;
    private final PartecipanteSistemazioneRepository
            partecipanteSistemazioneRepository;
    private final PartecipanteRepository partecipanteRepository;
    private final PartecipanteSistemazioneService partecipanteSistemazioneService;

    public AdminPartecipanteSistemazioneController(
            AdesioneRepository adesioneRepository,
            SistemazioneRepository sistemazioneRepository,
            CameraRepository cameraRepository,
            PartecipanteSistemazioneRepository
                    partecipanteSistemazioneRepository,
            PartecipanteRepository partecipanteRepository,
            PartecipanteSistemazioneService partecipanteSistemazioneService) {

        this.adesioneRepository = adesioneRepository;
        this.sistemazioneRepository = sistemazioneRepository;
        this.cameraRepository = cameraRepository;
        this.partecipanteSistemazioneRepository =
                partecipanteSistemazioneRepository;
        this.partecipanteRepository = partecipanteRepository;
        this.partecipanteSistemazioneService = partecipanteSistemazioneService;
    }


    @GetMapping
    public String elenco(
            @PathVariable Integer adesioneId,
            Model model) {

        Adesione adesione = adesioneRepository
                .findById(adesioneId)
                .orElseThrow();

        Integer partecipanteId =
                adesione.getPartecipante().getId();

        List<PartecipanteSistemazione> sistemazioni =
                partecipanteSistemazioneRepository
                        .findByPartecipanteAndPellegrinaggio(
                                adesione.getPartecipante().getId(),
                                adesione.getPellegrinaggio().getId()
                        );
        
        List<Sistemazione> sistemazioniDisponibili =
                sistemazioneRepository
                        .findByIdPellegrinaggio(
                                adesione.getPellegrinaggio().getId()
                        );
        
        List<Partecipante> partecipanti =
                partecipanteRepository
                        .findByAdesioniPellegrinaggioIdExcludingPartecipante(
                                adesione.getPellegrinaggio().getId(),
                                adesione.getPartecipante().getId()
                        );
        
        
        Map<Integer, String> camerePartecipanti = new HashMap<>();
        Map<Integer, Integer> sistemazioniPartecipanti = new HashMap<>();
        Map<Integer, String> nomiSistemazioniPartecipanti = new HashMap<>();

       


        for (Partecipante partecipante : partecipanti) {

            List<PartecipanteSistemazione> sistemazioniPartecipante =
                    partecipanteSistemazioneRepository
                            .findByPartecipanteAndPellegrinaggio(
                                    partecipante.getId(),
                                    adesione.getPellegrinaggio().getId()
                            );

            if (!sistemazioniPartecipante.isEmpty()) {

                PartecipanteSistemazione ps =
                        sistemazioniPartecipante.get(0);

                camerePartecipanti.put(
                        partecipante.getId(),
                        ps.getCamera().getDescrizione()
                );

                sistemazioniPartecipanti.put(
                        partecipante.getId(),
                        ps.getCamera().getSistemazione().getId()
                );
                
                // Nome sistemazione

                nomiSistemazioniPartecipanti.put(
                        partecipante.getId(),
                        ps.getCamera()
                                .getSistemazione()
                                .getDescrizione()
                );

            }
        }



        model.addAttribute("adesione", adesione);
        model.addAttribute("sistemazioni", sistemazioni);
        model.addAttribute("sistemazioniDisponibili", sistemazioniDisponibili);
        model.addAttribute("partecipanti", partecipanti);
        model.addAttribute("camerePartecipanti",camerePartecipanti);
        model.addAttribute("sistemazioniPartecipanti",sistemazioniPartecipanti);
        model.addAttribute("nomiSistemazioniPartecipanti",nomiSistemazioniPartecipanti);



        return "admin/adesioni/sistemazioni/elenco";
    }

    
    @PostMapping
    public String salva(
    		@PathVariable("adesioneId") Integer id,
            @RequestParam Integer idSistemazione,
            @RequestParam(required = false) Integer idCamera,
            @RequestParam(required = false) String nuovaCamera,
            @RequestParam(
                    name = "idPartecipanti",
                    required = false
            )
            List<Integer> idPartecipanti) {


        /*
         * Recuperiamo l'adesione
         */
        Adesione adesione =
                adesioneRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Adesione non trovata"
                                )
                        );


        /*
         * Aggiungiamo sempre il partecipante
         * principale alla lista.
         */
        if (idPartecipanti == null) {

            idPartecipanti =
                    new ArrayList<>();
        }


        Integer idPartecipante =
                adesione
                        .getPartecipante()
                        .getId();


        if (!idPartecipanti.contains(idPartecipante)) {

            idPartecipanti.add(
                    idPartecipante
            );
        }


        /*
         * Salviamo la sistemazione.
         */
        partecipanteSistemazioneService.assegnaPartecipanti(
                id,
                idSistemazione,
                idCamera,
                nuovaCamera,
                idPartecipanti
        );


        return "redirect:/admin/adesioni/"
                + id
                + "/sistemazioni";
    }
 
 



    @PostMapping("/{idPartecipanteSistemazione}/elimina")
    public String elimina(
            @PathVariable("adesioneId")
            Integer adesioneId,

            @PathVariable("idPartecipanteSistemazione")
            Integer idPartecipanteSistemazione) {

        PartecipanteSistemazione ps =
                partecipanteSistemazioneRepository
                        .findById(idPartecipanteSistemazione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Associazione partecipante-sistemazione non trovata"
                                )
                        );

        /*
         * Controlliamo che l'associazione appartenga
         * realmente al partecipante dell'adesione.
         */
        if (!ps.getPartecipante()
                .getId()
                .equals(
                        adesioneRepository
                                .findById(adesioneId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Adesione non trovata"
                                        )
                                )
                                .getPartecipante()
                                .getId()
                )) {

            throw new IllegalArgumentException(
                    "L'associazione non appartiene al partecipante"
            );
        }


        partecipanteSistemazioneService.elimina(
                ps.getPartecipante().getId(),
                idPartecipanteSistemazione
        );


        return "redirect:/admin/adesioni/"
                + adesioneId
                + "/sistemazioni";
    }



}

