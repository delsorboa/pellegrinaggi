package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.*;
import it.pellegrinaggi.service.AdesioneService;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Controller
@RequestMapping("/admin/adesioni")
public class AdminAdesioneController {


    private final AdesioneRepository adesioneRepository;
    private final PartecipanteRepository partecipanteRepository;
    private final PellegrinaggioRepository pellegrinaggioRepository;
    private final AdesioneService adesioneService;


    public AdminAdesioneController(
            AdesioneRepository adesioneRepository,
            PartecipanteRepository partecipanteRepository,
            PellegrinaggioRepository pellegrinaggioRepository,
            AdesioneService adesioneService
    ) {

        this.adesioneRepository = adesioneRepository;
        this.partecipanteRepository = partecipanteRepository;
        this.pellegrinaggioRepository = pellegrinaggioRepository;
        this.adesioneService = adesioneService;
    }


    @GetMapping
    public String lista(
            @RequestParam(required = false) Integer idPellegrinaggio,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        /*
         * Evita pagine negative
         */
        if (page < 0) {
            page = 0;
        }

        /*
         * =====================================================
         * PAGINAZIONE
         * =====================================================
         */
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("partecipante.cognome"),
                        Sort.Order.asc("partecipante.nome")
                )
        );

        /*
         * =====================================================
         * FILTRO
         * =====================================================
         */
        if (nome != null) {
            nome = nome.trim();
        }

        /*
         * =====================================================
         * ADESIONI (Ricerca Paginata)
         * =====================================================
         */
        Page<Adesione> paginaAdesioni = adesioneRepository.cerca(
                idPellegrinaggio,
                nome,
                pageable
        );

        /*
         * =====================================================
         * PELLEGRINAGGI (Per la combo di filtraggio)
         * =====================================================
         */
        List<Pellegrinaggio> pellegrinaggi = pellegrinaggioRepository.findAll();

        /*
         * =====================================================
         * MODEL (Allineato con i parametri dell'HTML)
         * =====================================================
         */
        // Passiamo solo la lista scompattata degli elementi della pagina corrente
        model.addAttribute("adesioni", paginaAdesioni.getContent());
        
        // Contatori necessari alla barra di paginazione Bootstrap
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaAdesioni.getTotalPages());
        model.addAttribute("totalItems", paginaAdesioni.getTotalElements());

        // Dati per la persistenza dei filtri nel form di ricerca
        model.addAttribute("pellegrinaggi", pellegrinaggi);
        model.addAttribute("idPellegrinaggio", idPellegrinaggio);
        model.addAttribute("nome", nome);

        // Ritorna la vista coerente con il nome del file modificato
        return "admin/adesioni/lista";
    }

    @GetMapping("/nuova")
    public String nuova(Model model) {
    	
        // Corretto da null a istanza vuota per evitare eccezioni nel binding th:object del form
        model.addAttribute(
                "adesione",
                new Adesione()
        );

        model.addAttribute(
                "partecipanti",
                partecipanteRepository.findAll()
        );

        model.addAttribute(
                "pellegrinaggi",
                pellegrinaggioRepository.findByStato(
                        StatoPellegrinaggio.APERTO
                )
        );

        return "admin/adesioni/form";
    }



    @GetMapping("/modifica/{id}")
    public String modifica(
            @PathVariable Integer id,
            Model model) {

        Adesione adesione =
                adesioneRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Adesione non trovata: " + id
                        )
                );


        model.addAttribute(
                "adesione",
                adesione
        );


        model.addAttribute(
                "partecipanti",
                partecipanteRepository.findAll()
        );


        model.addAttribute(
                "pellegrinaggi",
                pellegrinaggioRepository.findByStato(
                        StatoPellegrinaggio.APERTO
                )
        );


        return "admin/adesioni/form";
    }



    // =========================================================
    // SALVA / MODIFICA
    // =========================================================

    @PostMapping("/salva")
    public String salva(

            @RequestParam(required = false)
            Integer id,

            @RequestParam Integer partecipanteId,

            @RequestParam Integer pellegrinaggioId,

            @RequestParam BigDecimal quota,

            @RequestParam String stato

    ) {


        Partecipante partecipante =
                partecipanteRepository
                .findById(partecipanteId)
                .orElseThrow();


        Pellegrinaggio pellegrinaggio =
                pellegrinaggioRepository
                .findById(pellegrinaggioId)
                .orElseThrow();


        // =====================================================
        // NUOVA ADESIONE
        // =====================================================

        if (id == null) {

            long iscritti =
                    adesioneRepository.countByPellegrinaggio(
                        pellegrinaggio
                    );


            if (iscritti >= pellegrinaggio.getPostiTotali()) {

                throw new RuntimeException(
                    "Posti esauriti"
                );
            }


            if (
                adesioneRepository
                .existsByPartecipanteAndPellegrinaggio(
                    partecipante,
                    pellegrinaggio
                )
            ) {

                throw new RuntimeException(
                    "Il partecipante è già iscritto a questo pellegrinaggio"
                );
            }


            // La quota viene presa dal costo del pellegrinaggio
            quota = pellegrinaggio.getCosto();


            adesioneService.creaAdesioneAdmin(
                partecipante,
                pellegrinaggio,
                quota,
                stato
            );

        }

        // =====================================================
        // MODIFICA ADESIONE
        // =====================================================

        else {

            Adesione adesione =
                    adesioneRepository
                    .findById(id)
                    .orElseThrow();


            /*
             * Controlliamo che non esista già un'altra
             * adesione dello stesso partecipante
             * allo stesso pellegrinaggio.
             *
             * L'adesione che stiamo modificando viene esclusa.
             */
            if (
                adesioneRepository
                .existsByPartecipanteAndPellegrinaggioAndIdNot(
                    partecipante,
                    pellegrinaggio,
                    id
                )
            ) {

                throw new RuntimeException(
                    "Il partecipante è già iscritto a questo pellegrinaggio"
                );
            }


            /*
             * Controlliamo i posti solo se l'adesione
             * viene spostata su un pellegrinaggio diverso.
             */
            if (
                !adesione.getPellegrinaggio()
                         .getId()
                         .equals(pellegrinaggio.getId())
            ) {

                long iscritti =
                        adesioneRepository.countByPellegrinaggio(
                            pellegrinaggio
                        );


                if (
                    iscritti >= pellegrinaggio.getPostiTotali()
                ) {

                    throw new RuntimeException(
                        "Posti esauriti"
                    );
                }
            }


            // Aggiorno i dati
            adesione.setPartecipante(partecipante);

            adesione.setPellegrinaggio(pellegrinaggio);

            adesione.setStato(stato);

            // La quota viene sempre presa dal pellegrinaggio
            adesione.setQuota(
                pellegrinaggio.getCosto()
            );


            adesioneRepository.save(adesione);
        }


        return "redirect:/admin/adesioni";
    }
}
