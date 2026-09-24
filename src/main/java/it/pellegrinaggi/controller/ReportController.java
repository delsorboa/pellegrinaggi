package it.pellegrinaggi.controller;


import it.pellegrinaggi.model.Pagamento;
import it.pellegrinaggi.model.Pellegrinaggio;
import it.pellegrinaggi.repository.PagamentoRepository;
import it.pellegrinaggi.repository.PellegrinaggioRepository;
import it.pellegrinaggi.service.PdfService;


import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/report")
public class ReportController {



private final PdfService pdfService;

private final PellegrinaggioRepository repository;

private final PagamentoRepository pagamentoRepository;



public ReportController(
        PdfService pdfService,
        PellegrinaggioRepository repository,
        PagamentoRepository pagamentoRepository){

    this.pdfService = pdfService;
    this.repository = repository;
    this.pagamentoRepository = pagamentoRepository;

}



@GetMapping("/partecipanti/{id}")
public ResponseEntity<byte[]> partecipanti(
        @PathVariable Integer id)
        throws Exception {



    Pellegrinaggio p =
        repository.findById(id)
        .orElseThrow();



    byte[] pdf =
        pdfService.listaPartecipanti(p);



    return ResponseEntity.ok()

        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=partecipanti.pdf"
        )

        .contentType(
            MediaType.APPLICATION_PDF
        )

        .body(pdf);


}

@GetMapping("/ricevuta/{id}")
public ResponseEntity<byte[]> ricevuta(
        @PathVariable Integer id)
        throws Exception {


    Pagamento pagamento =
        pagamentoRepository
        .findById(id)
        .orElseThrow();


    byte[] pdf =
        pdfService.ricevutaPagamento(
            pagamento
        );


    return ResponseEntity.ok()

        .header(
          HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=ricevuta.pdf"
        )

        .contentType(
          MediaType.APPLICATION_PDF
        )

        .body(pdf);

}

@GetMapping("/economico/{id}")
public ResponseEntity<byte[]> economico(
        @PathVariable Integer id)
        throws Exception {


    Pellegrinaggio p =
        repository.findById(id)
        .orElseThrow();


    byte[] pdf =
        pdfService.situazioneEconomica(p);



    return ResponseEntity.ok()

        .header(
          HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=economia.pdf"
        )

        .contentType(
          MediaType.APPLICATION_PDF
        )

        .body(pdf);

}


}