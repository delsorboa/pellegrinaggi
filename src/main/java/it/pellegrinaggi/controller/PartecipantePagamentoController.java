package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.Adesione;
import it.pellegrinaggi.model.Pagamento;
import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.AdesioneRepository;
import it.pellegrinaggi.repository.PagamentoRepository;
import it.pellegrinaggi.service.UtenteService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/partecipante/pagamenti")
public class PartecipantePagamentoController {


	private final AdesioneRepository adesioneRepository;
	private final PagamentoRepository pagamentoRepository;
	private final UtenteService utenteService;



	public PartecipantePagamentoController(
	        AdesioneRepository adesioneRepository,
	        PagamentoRepository pagamentoRepository,
	        UtenteService utenteService
	){

	    this.adesioneRepository = adesioneRepository;
	    this.pagamentoRepository = pagamentoRepository;
	    this.utenteService = utenteService;

	}



    @GetMapping("/{id}")
    public String elenco(
            @PathVariable Integer id,
            Authentication authentication,
            Model model
    ){

        Utente utente =
                utenteService
                .getUtenteLoggato(authentication);



        Adesione adesione =
                adesioneRepository
                .findById(id)
                .orElseThrow();



        if(
            !adesione.getUtente()
            .getId()
            .equals(utente.getId())
        ){

            throw new RuntimeException(
                    "Non puoi visualizzare questa adesione"
            );

        }



        model.addAttribute(
                "adesione",
                adesione
        );


        model.addAttribute(
                "pagamenti",
                pagamentoRepository
                .findByAdesione(adesione)
        );


        return "partecipante/pagamenti";

    }
    
    @GetMapping("/nuovo/{adesioneId}")
    public String nuovo(
            @PathVariable Integer adesioneId,
            Authentication authentication,
            Model model
    ){

        Utente utente =
                utenteService
                .getUtenteLoggato(authentication);


        Adesione adesione =
                adesioneRepository
                .findById(adesioneId)
                .orElseThrow();



        if(!adesione.getUtente()
                .getId()
                .equals(utente.getId())){

            throw new RuntimeException(
                    "Non puoi inserire pagamenti per questa adesione"
            );

        }



        Pagamento pagamento =
                new Pagamento();


        pagamento.setAdesione(
                adesione
        );


        model.addAttribute(
                "pagamento",
                pagamento
        );


        model.addAttribute(
                "adesione",
                adesione
        );


        return "partecipante/pagamento-form";

    }
    
    @PostMapping("/salva")
    public String salva(
            @ModelAttribute Pagamento pagamento,
            Authentication authentication
    ){

        Utente utente =
                utenteService
                .getUtenteLoggato(authentication);


        Adesione adesione =
                adesioneRepository
                .findById(
                    pagamento.getAdesione().getId()
                )
                .orElseThrow();


        if(!adesione.getUtente()
                .getId()
                .equals(utente.getId())){

            throw new RuntimeException(
                    "Operazione non consentita"
            );

        }


        pagamento.setStatoPagamento(
                "INSERITO"
        );


        pagamentoRepository.save(
                pagamento
        );


        return "redirect:/partecipante/pagamenti/"
                + adesione.getId();

    }

}
