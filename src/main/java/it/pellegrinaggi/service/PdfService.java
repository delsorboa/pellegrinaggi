package it.pellegrinaggi.service;


import it.pellegrinaggi.model.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;



@Service
public class PdfService {



public byte[] listaPartecipanti(
        Pellegrinaggio pellegrinaggio)
        throws Exception {


    ByteArrayOutputStream out =
            new ByteArrayOutputStream();



    Document document =
            new Document();



    PdfWriter.getInstance(
            document,
            out
    );



    document.open();



    Font titolo =
            FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                18
            );


    document.add(
        new Paragraph(
            "Lista partecipanti",
            titolo
        )
    );


    document.add(
        new Paragraph(
            "Pellegrinaggio: "
            + pellegrinaggio.getNome()
        )
    );


    document.add(
        new Paragraph(
            "Destinazione: "
            + pellegrinaggio.getDestinazione()
        )
    );


    document.add(
        new Paragraph(" ")
    );



    PdfPTable table =
            new PdfPTable(3);


    table.addCell("Nome");
    table.addCell("Cognome");
    table.addCell("Stato");



    for(Adesione a :
        pellegrinaggio.getAdesioni()){


        table.addCell(
            a.getPartecipante()
             .getNome()
        );


        table.addCell(
            a.getPartecipante()
             .getCognome()
        );


        table.addCell(
            a.getStato()
             .toString()
        );

    }


    document.add(table);


    document.close();


    return out.toByteArray();

}

public byte[] ricevutaPagamento(
        Pagamento pagamento)
        throws Exception {


    ByteArrayOutputStream out =
            new ByteArrayOutputStream();


    Document document =
            new Document();


    PdfWriter.getInstance(
            document,
            out
    );


    document.open();



    Font titolo =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    18
            );



    document.add(
        new Paragraph(
            "RICEVUTA PAGAMENTO",
            titolo
        )
    );


    document.add(
        new Paragraph(" ")
    );


    document.add(
        new Paragraph(
            "Partecipante: "
            +
            pagamento.getAdesione()
            .getPartecipante()
            .getNome()
            +
            " "
            +
            pagamento.getAdesione()
            .getPartecipante()
            .getCognome()
        )
    );


    document.add(
        new Paragraph(
            "Pellegrinaggio: "
            +
            pagamento.getAdesione()
            .getPellegrinaggio()
            .getNome()
        )
    );


    document.add(
        new Paragraph(
            "Importo: "
            +
            pagamento.getImporto()
            +
            " €"
        )
    );


    document.add(
        new Paragraph(
            "Data: "
            +
            pagamento.getDataPagamento()
        )
    );


    document.add(
        new Paragraph(
            "Causale: "
            +
            pagamento.getCausale()
        )
    );


    document.close();


    return out.toByteArray();

}

public byte[] situazioneEconomica(
        Pellegrinaggio p)
        throws Exception {


    ByteArrayOutputStream out =
            new ByteArrayOutputStream();


    Document document =
            new Document();


    PdfWriter.getInstance(
            document,
            out
    );


    document.open();


    document.add(
        new Paragraph(
            "SITUAZIONE ECONOMICA"
        )
    );


    document.add(
        new Paragraph(
            "Pellegrinaggio: "
            + p.getNome()
        )
    );


    document.add(
        new Paragraph(
            "Destinazione: "
            + p.getDestinazione()
        )
    );



    double previsto = 0;

    double incassato = 0;



    for(Adesione a :
        p.getAdesioni()){


        if(a.getQuota()!=null){

            previsto +=
                a.getQuota()
                .doubleValue();

        }



//        for(Pagamento pagamento :
//            a.getPagamenti()){
//
//
//            incassato +=
//                pagamento
//                .getImporto()
//                .doubleValue();
//
//        }

    }



    document.add(
        new Paragraph(
            "Quote previste: "
            + previsto
            + " €"
        )
    );


    document.add(
        new Paragraph(
            "Incassato: "
            + incassato
            + " €"
        )
    );


    document.add(
        new Paragraph(
            "Da incassare: "
            +
            (previsto-incassato)
            +
            " €"
        )
    );



    document.close();


    return out.toByteArray();

}


}