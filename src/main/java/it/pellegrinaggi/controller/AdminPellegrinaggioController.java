package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.StatoPellegrinaggio;
import it.pellegrinaggi.repository.DestinazioneRepository;
import it.pellegrinaggi.repository.PellegrinaggioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin/pellegrinaggi")
public class AdminPellegrinaggioController {


    private final PellegrinaggioRepository repository;
    
    private final DestinazioneRepository destinazioneRepository;


    public AdminPellegrinaggioController(
            PellegrinaggioRepository repository,
            DestinazioneRepository destinazioneRepository){

        this.repository = repository;
        this.destinazioneRepository = destinazioneRepository;
    }



    @GetMapping
    public String elenco(@RequestParam(name = "nome", required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        // Paginazione ordinata per data di partenza cronologica
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.asc("dataPartenza"))
        );

        Page<Pellegrinaggio> paginaPellegrinaggi;

        if (nome != null && !nome.isBlank()) {
            paginaPellegrinaggi = repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        } else {
            paginaPellegrinaggi = repository.findAll(pageable);
        }

        // Invio dati all'HTML
        model.addAttribute("pellegrinaggi", paginaPellegrinaggi.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaPellegrinaggi.getTotalPages());
        model.addAttribute("totalItems", paginaPellegrinaggi.getTotalElements());
        model.addAttribute("nome", nome);

        return "admin/pellegrinaggi/elenco"; // Assicurati che corrisponda al nome file corretto
    }




    @GetMapping("/nuovo")
    public String nuovo(Model model){

        Pellegrinaggio p =
                new Pellegrinaggio();


        p.setStato(
                StatoPellegrinaggio.APERTO
        );
        
        
        model.addAttribute("destinazioni", destinazioneRepository.findAll());


        model.addAttribute(
                "pellegrinaggio",
                p
        );


        return "admin/pellegrinaggi/form";
    }



    @PostMapping("/salva")
    public String salva(
            @Valid @ModelAttribute Pellegrinaggio pellegrinaggio,
            BindingResult result,
            Model model){


        if(result.hasErrors()){

            return "admin/pellegrinaggi/form";

        }


        repository.save(pellegrinaggio);


        return "redirect:/admin/pellegrinaggi";

    }



    @GetMapping("/modifica/{id}")
    public String modifica(
            @PathVariable Integer id,
            Model model){


        model.addAttribute(
                "pellegrinaggio",
                repository.findById(id)
                .orElseThrow()
        );
        
        model.addAttribute("destinazioni", destinazioneRepository.findAll());


        return "admin/pellegrinaggi/form";

    }



    @GetMapping("/elimina/{id}")
    public String elimina(
            @PathVariable Integer id){

        repository.deleteById(id);

        return "redirect:/admin/pellegrinaggi";
    }

}