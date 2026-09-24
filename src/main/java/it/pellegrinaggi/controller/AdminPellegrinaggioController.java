package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.model.StatoPellegrinaggio;
import it.pellegrinaggi.repository.DestinazioneRepository;
import it.pellegrinaggi.repository.PellegrinaggioRepository;


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
    public String elenco(Model model){

        model.addAttribute(
                "pellegrinaggi",
                repository.findAll()
        );

        return "admin/pellegrinaggi/elenco";
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