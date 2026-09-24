package it.pellegrinaggi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponsabileSpostamentoReport {

    private String cognome;

    private String nome;
    
    
    public String getNominativo() {
        return cognome + " " + nome;
    }
}

