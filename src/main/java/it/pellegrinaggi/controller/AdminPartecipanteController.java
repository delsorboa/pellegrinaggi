package it.pellegrinaggi.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import it.pellegrinaggi.model.Partecipante;
import it.pellegrinaggi.repository.PartecipanteRepository;
import it.pellegrinaggi.service.AccountService;
import it.pellegrinaggi.service.GradoService;
import it.pellegrinaggi.service.QualificaService;


@Controller
@RequestMapping("/admin/partecipanti")
public class AdminPartecipanteController {


    private final PartecipanteRepository repository;
    
    private final AccountService accountService;
    
    private final QualificaService qualificaService;
    
    private final GradoService gradoService;


    public AdminPartecipanteController(
            PartecipanteRepository repository,
            AccountService accountService,
            QualificaService qualificaService,
            GradoService gradoService){

        this.repository = repository;
        this.accountService = accountService;
        this.qualificaService = qualificaService;
        this.gradoService = gradoService;

    }



    @GetMapping
    public String listaPartecipanti(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "cognome", required = false) String cognome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        // Evita pagine negative
        if (page < 0) {
            page = 0;
        }

        // Configurazione della paginazione e ordinamento alfabetico per Cognome e Nome
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("cognome"),
                        Sort.Order.asc("nome")
                )
        );

        // Usiamo un'unica variabile di tipo Page per gestire la paginazione in tutti i casi
        Page<Partecipante> paginaPartecipanti;

        // Se entrambi i filtri sono valorizzati
        if (nome != null && !nome.isBlank() && cognome != null && !cognome.isBlank()) {
            paginaPartecipanti = repository.findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCase(nome, cognome, pageable);
        } 
        // Se è presente solo il filtro Nome
        else if (nome != null && !nome.isBlank()) {
            paginaPartecipanti = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } 
        // Se è presente solo il filtro Cognome
        else if (cognome != null && !cognome.isBlank()) {
            paginaPartecipanti = repository.findByCognomeContainingIgnoreCase(cognome, pageable);
        } 
        // Se non ci sono filtri, mostra l'elenco completo paginato
        else {
            paginaPartecipanti = repository.findAll(pageable);
        }

        // Passiamo alla tabella HTML solo il contenuto della pagina corrente (List<Partecipante>)
        model.addAttribute("partecipanti", paginaPartecipanti.getContent());
        
        // Dati aggiuntivi fondamentali per gestire i bottoni della paginazione nell'HTML
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaPartecipanti.getTotalPages());
        model.addAttribute("totalItems", paginaPartecipanti.getTotalElements());

        return "admin/partecipanti/elenco";
    }






    @GetMapping("/nuovo")
    public String nuovo(Model model){


        model.addAttribute(
                "partecipante",
                new Partecipante()
        );
        
        model.addAttribute("qualifiche", qualificaService.findAll());
        model.addAttribute("gradi", gradoService.findAll());


        return "admin/partecipanti/form";

    }





    @PostMapping("/salva")
    public String salva(
            @ModelAttribute Partecipante partecipante,
            @RequestParam(required = false) boolean creaAccount,
            Model model){
    	
    	
        Partecipante salvato =
                repository.save(partecipante);



        if(creaAccount){


            String password =
                    accountService.creaAccount(salvato);



            model.addAttribute(
                    "passwordCreata",
                    password
            );


            return "admin/partecipanti/password-creata";

        }



        return "redirect:/admin/partecipanti";

    }





    @GetMapping("/modifica/{id}")
    public String modifica(
            @PathVariable Integer id,
            Model model){


        Partecipante p =
                repository.findById(id)
                .orElseThrow();
        
        
        model.addAttribute("qualifiche", qualificaService.findAll());
        model.addAttribute("gradi", gradoService.findAll());



        model.addAttribute(
                "partecipante",
                p
        );


        return "admin/partecipanti/form";

    }





    @GetMapping("/elimina/{id}")
    public String elimina(
            @PathVariable Integer id){


        repository.deleteById(id);


        return "redirect:/admin/partecipanti";

    }

}
