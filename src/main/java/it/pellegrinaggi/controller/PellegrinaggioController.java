package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.repository.PellegrinaggioRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/pellegrinaggi")
public class PellegrinaggioController {


    private final PellegrinaggioRepository repository;


    public PellegrinaggioController(
            PellegrinaggioRepository repository) {

        this.repository = repository;
    }



    @GetMapping
    public String elenco(Model model) {

        model.addAttribute(
                "lista",
                repository.findAll()
        );

        return "pellegrinaggi";
    }



    @GetMapping("/nuovo")
    public String nuovo(Model model) {

        model.addAttribute(
                "pellegrinaggio",
                new Pellegrinaggio()
        );

        return "pellegrinaggio-form";
    }



    @PostMapping("/salva")
    public String salva(
            @ModelAttribute Pellegrinaggio p) {


        repository.save(p);

        return "redirect:/pellegrinaggi";
    }



    @GetMapping("/modifica/{id}")
    public String modifica(
            @PathVariable Integer id,
            Model model) {


        Pellegrinaggio p =
                repository.findById(id)
                .orElseThrow();


        model.addAttribute(
                "pellegrinaggio",
                p
        );


        return "pellegrinaggio-form";
    }



    @GetMapping("/elimina/{id}")
    public String elimina(
            @PathVariable Integer id) {


        repository.deleteById(id);


        return "redirect:/pellegrinaggi";
    }



    @GetMapping("/{id}")
    public String dettaglio(
            @PathVariable Integer id,
            Model model) {


        Pellegrinaggio p =
                repository.findById(id)
                .orElseThrow();


        model.addAttribute(
                "pellegrinaggio",
                p
        );


        return "pellegrinaggio-dettaglio";
    }

}