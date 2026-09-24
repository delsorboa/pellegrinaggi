package it.pellegrinaggi.controller;

import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.*;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/pagamenti")
public class AdminPagamentoController {


    private final PagamentoRepository pagamentoRepository;

    private final AdesioneRepository adesioneRepository;



    public AdminPagamentoController(
            PagamentoRepository pagamentoRepository,
            AdesioneRepository adesioneRepository
    ){

        this.pagamentoRepository = pagamentoRepository;
        this.adesioneRepository = adesioneRepository;

    }




    @GetMapping("/adesione/{id}")
    public String elenco(
            @PathVariable Integer id,
            Model model
    ){

        Adesione adesione =
                adesioneRepository
                .findById(id)
                .orElseThrow();



        BigDecimal totalePagato =
                pagamentoRepository
                .totalePagato(adesione);

      
        BigDecimal quota =
                adesione.getQuota() != null
                ? adesione.getQuota()
                : BigDecimal.ZERO;



        BigDecimal residuo =
                quota.subtract(totalePagato);



        String statoPagamento;


        if(totalePagato.compareTo(BigDecimal.ZERO)==0){

            statoPagamento = "DA PAGARE";

        }
        else if(residuo.compareTo(BigDecimal.ZERO)>0){

            statoPagamento = "PARZIALE";

        }
        else{

            statoPagamento = "SALDATO";

        }



        model.addAttribute(
                "adesione",
                adesione
        );


        model.addAttribute(
                "pagamenti",
                pagamentoRepository.findByAdesione(adesione)
        );



        model.addAttribute(
                "quota",
                quota
        );


        model.addAttribute(
                "totalePagato",
                totalePagato
        );


        model.addAttribute(
                "residuo",
                residuo
        );


        model.addAttribute(
                "statoPagamento",
                statoPagamento
        );



        return "admin/pagamenti/lista";

    }



    @GetMapping("/nuovo/{adesioneId}")
    public String nuovo(
            @PathVariable Integer adesioneId,
            Model model
    ){


        Adesione adesione =
                adesioneRepository
                .findById(adesioneId)
                .orElseThrow();



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


        return "admin/pagamenti/nuovo";

    }
    
    @PostMapping("/salva")
    public String salva(
            @RequestParam Integer adesioneId,
            @RequestParam BigDecimal importo,
            @RequestParam String causale,
            @RequestParam String note,
            @RequestParam TipoPagamento tipoPagamento
    ){


        Adesione adesione =
                adesioneRepository
                .findById(adesioneId)
                .orElseThrow();



        Pagamento pagamento =
                new Pagamento();


        pagamento.setAdesione(
                adesione
        );


        pagamento.setImporto(
                importo
        );


        pagamento.setCausale(
                causale
        );


        pagamento.setNote(
                note
        );


        pagamento.setTipoPagamento(
                tipoPagamento
        );


        pagamentoRepository.save(
                pagamento
        );


        return "redirect:/admin/pagamenti/adesione/"
                + adesioneId;

    }
    
    @PostMapping("/conferma/{id}")
    public String conferma(
            @PathVariable Integer id
    ){

        Pagamento pagamento =
                pagamentoRepository
                .findById(id)
                .orElseThrow();



        pagamento.setStatoPagamento(
                "CONFERMATO"
        );


        pagamentoRepository.save(
                pagamento
        );


        return "redirect:/admin/pagamenti/adesione/"
                + pagamento.getAdesione().getId();

    }
    
    @PostMapping("/rifiuta/{id}")
    public String rifiuta(
            @PathVariable Integer id
    ){

        Pagamento pagamento =
                pagamentoRepository
                .findById(id)
                .orElseThrow();



        pagamento.setStatoPagamento(
                "RIFIUTATO"
        );


        pagamentoRepository.save(
                pagamento
        );


        return "redirect:/admin/pagamenti/adesione/"
                + pagamento.getAdesione().getId();

    }


}
