package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.*;
import it.pellegrinaggi.service.AdesioneService;
import it.pellegrinaggi.service.SpostamentoService;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/adesioni")
public class AdminAdesioneSpostamentoController {

    private final AdesioneService adesioneService;
    private final SpostamentoService spostamentoService;
    
    

    public AdminAdesioneSpostamentoController(AdesioneService adesioneService, SpostamentoService spostamentoService) {
		this.adesioneService = adesioneService;
		this.spostamentoService = spostamentoService;
	}

	@GetMapping("/{id}/spostamenti")
    public String elenco(
            @PathVariable Integer id,
            Model model) {

        Adesione adesione = adesioneService.findById(id);

        List<Spostamento> spostamenti =
                spostamentoService.findByPartecipanteAndPellegrinaggio(
                        adesione.getPartecipante().getId(),
                        adesione.getPellegrinaggio().getId()
                );
        
        List<Spostamento> spostamentiDisponibili =
        		spostamentoService.findDisponibiliPerPartecipante(
                        adesione.getPellegrinaggio().getId(),
                        adesione.getPartecipante().getId()
                );

        model.addAttribute("adesione", adesione);
        model.addAttribute("spostamenti", spostamenti);
        model.addAttribute("spostamentiDisponibili", spostamentiDisponibili);

        return "admin/adesioni/spostamenti/elenco";
    }
	
	@PostMapping("/{id}/spostamenti/{idSpostamento}/abbina")
	public String abbinaSpostamento(
	        @PathVariable Integer id,
	        @PathVariable Integer idSpostamento,
	        RedirectAttributes redirectAttributes) {

	    adesioneService.associaSpostamento(
	    		id,
	            idSpostamento
	    );

	    redirectAttributes.addFlashAttribute(
	            "success",
	            "Spostamento abbinato correttamente."
	    );

	    return "redirect:/admin/adesioni/" + id + "/spostamenti";
	}


    @PostMapping("/{id}/spostamenti/{idSpostamento}/elimina")
    public String elimina(
            @PathVariable Integer id,
            @PathVariable Integer idSpostamento,
            RedirectAttributes redirectAttributes) {

        adesioneService.rimuoviSpostamento(id, idSpostamento);

        redirectAttributes.addFlashAttribute(
                "success",
                "Spostamento rimosso correttamente"
        );

        return "redirect:/admin/adesioni/" + id + "/spostamenti";
    }
}
