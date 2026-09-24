package it.pellegrinaggi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpostamentoReport {

    private Integer idMezzo;

    private String descrizioneMezzo;

    private String dataPartenza;

    private String dataArrivo;

    private String luogoPartenza;

    private String luogoArrivo;

    private String note;
    
    private String descrizioneViaggio;

    private List<ResponsabileSpostamentoReport> responsabili;
}
