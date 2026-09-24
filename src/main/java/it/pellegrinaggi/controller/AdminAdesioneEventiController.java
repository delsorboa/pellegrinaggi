package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.Adesione;
import it.pellegrinaggi.model.Eventi;
import it.pellegrinaggi.model.Partecipante;
import it.pellegrinaggi.model.PartecipanteEvento;
import it.pellegrinaggi.model.PartecipanteEventoView;
import it.pellegrinaggi.model.Servizio;
import it.pellegrinaggi.model.Tabella;
import it.pellegrinaggi.model.TipoRuolo;
import it.pellegrinaggi.model.ValoreTabella;
import it.pellegrinaggi.repository.AdesioneRepository;
import it.pellegrinaggi.repository.EventoRepository;
import it.pellegrinaggi.repository.PartecipanteEventoRepository;
import it.pellegrinaggi.repository.TabellaRepository;
import it.pellegrinaggi.repository.TipoEventoRepository;
import it.pellegrinaggi.repository.TipoRuoloRepository;
import it.pellegrinaggi.service.JasperReportService;
import it.pellegrinaggi.service.ValoreTabellaService;


import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/adesioni")
public class AdminAdesioneEventiController {

    private final AdesioneRepository adesioneRepository;

    private final EventoRepository eventoRepository;

    private final PartecipanteEventoRepository
            partecipanteEventoRepository;

    private final TipoEventoRepository tipoEventoRepository;

    private final TabellaRepository tabellaRepository;
    
    private final ValoreTabellaService valoreTabellaService;
    
    private final TipoRuoloRepository tipoRuoloRepository;

    private final JasperReportService jasperReportService;


    public AdminAdesioneEventiController(
            AdesioneRepository adesioneRepository,
            EventoRepository eventoRepository,
            PartecipanteEventoRepository partecipanteEventoRepository,
            TipoEventoRepository tipoEventoRepository,
            TabellaRepository tabellaRepository,
            ValoreTabellaService valoreTabellaService,
            TipoRuoloRepository tipoRuoloRepository,
            JasperReportService jasperReportService) {

        super();

        this.adesioneRepository =
                adesioneRepository;

        this.eventoRepository =
                eventoRepository;

        this.partecipanteEventoRepository =
                partecipanteEventoRepository;

        this.tipoEventoRepository =
                tipoEventoRepository;

        this.tabellaRepository =
                tabellaRepository;

        this.valoreTabellaService =
                valoreTabellaService;

        this.tipoRuoloRepository =
                tipoRuoloRepository;

        this.jasperReportService =
                jasperReportService;
    }


	/**
     * ELENCO EVENTI DELL'ADESIONE
     */
    @GetMapping("/{idAdesione}/eventi")
    public String elenco(
            @PathVariable Integer idAdesione,
            Model model) {

        Adesione adesione =
                adesioneRepository
                        .findById(idAdesione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Adesione non trovata"
                                ));


        model.addAttribute(
                "adesione",
                adesione
        );


        // =====================================================
        // EVENTI GIÀ ASSOCIATI
        // =====================================================
        
        
        List<PartecipanteEvento> partecipantiEventi =
                partecipanteEventoRepository
                        .findByIdPartecipanteAndIdPellegrinaggio(
                                adesione.getPartecipante().getId(),
                                adesione.getPellegrinaggio().getId()
                        );


        List<PartecipanteEventoView> eventi =
                partecipantiEventi.stream()
                        .map(pe -> {

                            String descrizioneEvento = "";

                            if (pe.getEvento() != null) {
                                descrizioneEvento =
                                        pe.getEvento().getDescrizione();
                            }


                            String descrizioneRuolo = "";

                            if (pe.getRuolo() != null) {
                                descrizioneRuolo =
                                        pe.getRuolo().getDescrizione();
                            }


                            String descrizioneValore = "";

                            if (pe.getTabella() != null
                                    && pe.getIdValoreTabella() != null) {

                                List<ValoreTabella> valori =
                                        valoreTabellaService.getValori(
                                                pe.getTabella().getId(),
                                                pe.getIdPellegrinaggio()
                                        );

                                descrizioneValore =
                                        valori.stream()
                                                .filter(v ->
                                                        v.getId().longValue()
                                                                == pe.getIdValoreTabella()
                                )
                                .map(ValoreTabella::getDescrizione)
                                .findFirst()
                                .orElse("");
                            }


                            return new PartecipanteEventoView(
                                    pe.getId(),
                                    descrizioneEvento,
                                    descrizioneRuolo,
                                    descrizioneValore,
                                    pe.getIdValoreTabella()
                            );
                        })
                        .toList();
                            
        // =====================================================
        // EVENTI DISPONIBILI
        // =====================================================

        List<Eventi> eventiDisponibili =
                eventoRepository.findEventiDisponibili(
                		adesione.getPellegrinaggio().getId(),
                		adesione.getPartecipante().getId()
                );
        
        // =====================================================
        // RUOLI
        // =====================================================

        List<TipoRuolo> ruoli =
                tipoRuoloRepository.findAll();


        // =====================================================
        // SERVIZI
        // =====================================================

        List<ValoreTabella> servizi =
        		valoreTabellaService
                        .getValori(2, adesione.getPellegrinaggio().getId());


        // =====================================================
        // PARTECIPANTI
        // =====================================================

        List<ValoreTabella> partecipanti =
        		valoreTabellaService
                .getValori(3, adesione.getPellegrinaggio().getId());
                      


        model.addAttribute(
                "eventi",
                eventi
        );
        
        model.addAttribute(
                "eventiDisponibili",
                eventiDisponibili
        );
        
        model.addAttribute(
                "valoriTabellaServizi",
                servizi
        );
        
        model.addAttribute(
                "valoriTabellaPartecipanti",
                partecipanti
        );
        
        model.addAttribute(
                "ruoli",
                ruoli
        );




        return "admin/adesioni/eventi/elenco";
    }
    
    
    @GetMapping("/{id}/eventi/elimina/{idEvento}")
    public String eliminaEvento(
            @PathVariable("id") Integer idAdesione,
            @PathVariable("idEvento") Integer idEvento,
            RedirectAttributes redirectAttributes) {

        try {

            partecipanteEventoRepository.deleteById(
                    idEvento
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Evento eliminato correttamente."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Errore durante l'eliminazione dell'evento."
            );
        }

        return "redirect:/admin/adesioni/" + idAdesione + "/eventi";
    }



    /**
     * NUOVO EVENTO PER L'ADESIONE
     */
    @GetMapping("/{idAdesione}/eventi/nuovo")
    public String nuovo(
            @PathVariable Integer idAdesione,
            Model model) {

        Adesione adesione =
                adesioneRepository
                        .findById(idAdesione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Adesione non trovata"
                                ));


        model.addAttribute(
                "adesione",
                adesione
        );


        model.addAttribute(
                "eventi",
                eventoRepository
                        .findByIdPellegrinaggio(
                                adesione.getPellegrinaggio().getId()
                        )
        );
        
        model.addAttribute(
                "ruoli",
                tipoRuoloRepository.findAll()
        );



        model.addAttribute(
                "tabelle",
                tabellaRepository.findAll()
        );


        model.addAttribute(
                "tipiEvento",
                tipoEventoRepository.findAll()
        );


        PartecipanteEvento partecipanteEvento =
                new PartecipanteEvento();


        partecipanteEvento.setIdPellegrinaggio(
                adesione.getPellegrinaggio().getId()
        );


        partecipanteEvento.setIdPartecipante(
                adesione.getPartecipante().getId()
        );


        model.addAttribute(
                "partecipanteEvento",
                partecipanteEvento
        );


        return "admin/adesioni/eventi/form";
    }
    

@PostMapping("/{idAdesione}/eventi/salva")
public String salva(
        @PathVariable Integer idAdesione,

        @RequestParam(required = false)
        Integer id,

        @RequestParam Integer idEvento,

        @RequestParam Integer idTabella,

        @RequestParam Integer idRuolo,

        @RequestParam Integer idValoreTabella,
        
        RedirectAttributes redirectAttributes) {


    Adesione adesione =
            adesioneRepository
                    .findById(idAdesione)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Adesione non trovata"
                            ));


    /*
     * NUOVO oppure MODIFICA
     */
    PartecipanteEvento partecipanteEvento;


    if (id != null) {

        partecipanteEvento =
                partecipanteEventoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evento partecipante non trovato"
                                ));

    } else {

        partecipanteEvento =
                new PartecipanteEvento();

    }
    
    Optional<PartecipanteEvento> esistente =
            partecipanteEventoRepository
                .findByIdPartecipanteAndEvento_Id(
                    adesione.getPartecipante().getId(),
                    idEvento
                );

    if (id == null && esistente.isPresent()) {

        redirectAttributes.addFlashAttribute(
            "errore",
            "Questo evento è già stato inserito per il partecipante."
        );

        return "redirect:/admin/adesioni/"
                + adesione.getId()
                + "/eventi";
    }



    /*
     * Dati dell'adesione
     */
    partecipanteEvento.setIdPellegrinaggio(
            adesione.getPellegrinaggio().getId()
    );


    partecipanteEvento.setIdPartecipante(
            adesione.getPartecipante().getId()
    );


    /*
     * EVENTO
     */
    Eventi evento =
            eventoRepository
                    .findById(idEvento)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Evento non trovato"
                            ));

    partecipanteEvento.setEvento(
            evento
    );


    /*
     * TABELLA
     */
    Tabella tabella =
            tabellaRepository
                    .findById(idTabella)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Tabella non trovata"
                            ));

    partecipanteEvento.setTabella(
            tabella
    );


    /*
     * VALORE
     */
    partecipanteEvento.setIdValoreTabella(
            Integer.valueOf(idValoreTabella)
    );


    /*
     * RUOLO
     */
    TipoRuolo ruolo =
            tipoRuoloRepository
                    .findById(idRuolo)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Ruolo non trovato"
                            ));

    partecipanteEvento.setRuolo(
            ruolo
    );


    /*
     * SALVA
     *
     * Se id == null → INSERT
     * Se id valorizzato → UPDATE
     */
    partecipanteEventoRepository.save(
            partecipanteEvento
    );


    return "redirect:/admin/adesioni/"
            + idAdesione
            + "/eventi";
}

    
    @GetMapping("/eventi/tabelle/{idTabella}/valori")
    @ResponseBody
    public List<ValoreTabella> valoriTabella(
            @PathVariable Integer idTabella,
            @RequestParam Integer idPellegrinaggio) {

        return valoreTabellaService.getValori(
                idTabella,
                idPellegrinaggio
        );
    }
    
    
    /**
     * MODIFICA EVENTO DELL'ADESIONE
     */
    @GetMapping("/{idAdesione}/eventi/modifica/{idEvento}")
    public String modifica(
            @PathVariable Integer idAdesione,
            @PathVariable Integer idEvento,
            Model model) {

        Adesione adesione =
                adesioneRepository
                        .findById(idAdesione)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Adesione non trovata"
                                ));


        PartecipanteEvento partecipanteEvento =
                partecipanteEventoRepository
                        .findById(idEvento)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evento partecipante non trovato"
                                ));


        /*
         * Verifichiamo che il record appartenga
         * effettivamente all'adesione.
         */
        if (!partecipanteEvento
                .getIdPartecipante()
                .equals(adesione.getPartecipante().getId())) {

            throw new IllegalArgumentException(
                    "L'evento non appartiene al partecipante"
            );
        }


        if (!partecipanteEvento
                .getIdPellegrinaggio()
                .equals(adesione.getPellegrinaggio().getId())) {

            throw new IllegalArgumentException(
                    "L'evento non appartiene al pellegrinaggio"
            );
        }


        model.addAttribute(
                "adesione",
                adesione
        );


        /*
         * Eventi disponibili per il pellegrinaggio
         */
        model.addAttribute(
                "eventi",
                eventoRepository
                        .findByIdPellegrinaggio(
                                adesione.getPellegrinaggio().getId()
                        )
        );


        /*
         * Ruoli
         */
        model.addAttribute(
                "ruoli",
                tipoRuoloRepository.findAll()
        );


        /*
         * Tabelle
         */
        model.addAttribute(
                "tabelle",
                tabellaRepository.findAll()
        );


        /*
         * Tipi evento
         */
        model.addAttribute(
                "tipiEvento",
                tipoEventoRepository.findAll()
        );


        /*
         * Oggetto già esistente.
         * Il form lo userà per la modifica.
         */
        model.addAttribute(
                "partecipanteEvento",
                partecipanteEvento
        );


        /*
         * Indichiamo al form che siamo in modifica.
         */
        model.addAttribute(
                "modifica",
                true
        );


        return "admin/adesioni/eventi/form";
    }
    
    @GetMapping("/eventi/{idEvento}/tabella")
    @ResponseBody
    public Integer getTabellaByEvento(
            @PathVariable Integer idEvento) {

        Eventi evento = eventoRepository.findById(idEvento)
                .orElseThrow(() ->
                    new RuntimeException("Evento non trovato"));

        if (evento.getTabella() == null) {
            return null;
        }

        return evento.getTabella().getId();
    }
    
    
    /**
     * GENERA FOGLIO DI SERVIZIO PDF
     */
//    @GetMapping("/{idAdesione}/ordine-servizio")
//    public ResponseEntity<byte[]> generaFoglioServizio(
//            @PathVariable Integer idAdesione) {
//
//        try {
//
//            Adesione adesione =
//                    adesioneRepository
//                            .findById(idAdesione)
//                            .orElseThrow(() ->
//                                    new IllegalArgumentException(
//                                            "Adesione non trovata"
//                                    )
//                            );
//
//
//            byte[] pdf =
//                    jasperReportService
//                            .generaFoglioServizio(
//                                    adesione.getId()
//                            );
//
//
//            return ResponseEntity
//                    .ok()
//                    .header(
//                            HttpHeaders.CONTENT_DISPOSITION,
//                            "inline; filename=foglio_servizio_"
//                                    + idAdesione
//                                    + ".pdf"
//                    )
//                    .contentType(
//                            MediaType.APPLICATION_PDF
//                    )
//                    .body(pdf);
//
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//            return ResponseEntity
//                    .internalServerError()
//                    .build();
//        }
//    }
    
    /**
     * GENERA FOGLIO DI SERVIZIO EXCEL
     */   
    @GetMapping("/{idAdesione}/ordine-servizio")
    public ResponseEntity<byte[]> generaFoglioServizio(
            @PathVariable Integer idAdesione) {

        try {

            Adesione adesione =
                    adesioneRepository
                            .findById(idAdesione)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Adesione non trovata"
                                    )
                            );

            byte[] excel =
                    jasperReportService
                            .generaFoglioServizio(
                                    adesione.getId()
                            );

            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=foglio_servizio_"
                                    + adesione.getPartecipante().getCognome()+"_"+adesione.getPartecipante().getNome()
                                    + ".xlsx"
                    )
                    .contentType(
                            MediaType.parseMediaType(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                            )
                    )
                    .body(excel);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }




}
