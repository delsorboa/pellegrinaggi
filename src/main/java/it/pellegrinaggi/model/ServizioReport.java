package it.pellegrinaggi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServizioReport {

    private Integer id;

    private String descrizione;

    private String valoreServizio;

    private String capiServizio;

    private String assistenzaMedica;

    private String assistenzaSpirituale;

    private String accompagna;

    private String con;


    // getter e setter
}
