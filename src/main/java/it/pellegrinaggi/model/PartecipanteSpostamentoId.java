package it.pellegrinaggi.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;


@Embeddable
@Data
public class PartecipanteSpostamentoId implements Serializable {

    @Column(name = "id_partecipante")
    private Integer idPartecipante;

    @Column(name = "id_spostamento")
    private Integer idSpostamento;

    
}
