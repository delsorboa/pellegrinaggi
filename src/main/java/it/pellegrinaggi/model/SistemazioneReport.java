package it.pellegrinaggi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SistemazioneReport {

    private Integer id;
    private Integer idPartecipante;
    private Integer idCamera;
    private Integer idSistemazione;

    private String descrizioneSistemazione;
    private String descrizioneCamera;
    private String alloggiaCon;

    // getter e setter
}
