package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;



@Controller
@RequestMapping("/pagamenti")
public class PagamentoController {


private final PagamentoRepository pagamentoRepository;

private final AdesioneRepository adesioneRepository;



public PagamentoController(
        PagamentoRepository pagamentoRepository,
        AdesioneRepository adesioneRepository){

    this.pagamentoRepository = pagamentoRepository;
    this.adesioneRepository = adesioneRepository;
}



@GetMapping("/nuovo/{adesioneId}")
public String nuovo(
        @PathVariable Integer adesioneId,
        Model model){


    Pagamento pagamento =
            new Pagamento();


    pagamento.setAdesione(
        adesioneRepository
        .findById(adesioneId)
        .orElseThrow()
    );


    model.addAttribute(
            "pagamento",
            pagamento
    );


    return "pagamento-form";

}



@PostMapping("/salva")
public String salva(
        @ModelAttribute Pagamento pagamento){


    pagamentoRepository.save(pagamento);


    return "redirect:/pellegrinaggi/"
    + pagamento.getAdesione()
            .getPellegrinaggio()
            .getId();

}



@GetMapping("/elimina/{id}")
public String elimina(
        @PathVariable Integer id){

    pagamentoRepository.deleteById(id);

    return "redirect:/";
}


}
