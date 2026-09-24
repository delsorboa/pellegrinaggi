package it.pellegrinaggi.controller;


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
    public String elenco(Model model){

        model.addAttribute(
                "partecipanti",
                repository.findAll()
        );


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
