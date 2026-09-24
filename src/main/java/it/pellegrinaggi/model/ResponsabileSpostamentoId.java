package it.pellegrinaggi.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ResponsabileSpostamentoId implements Serializable {

    @Column(name = "id_spostamento")
    private Integer idSpostamento;

    @Column(name = "id_partecipante")
    private Integer idPartecipante;

    public ResponsabileSpostamentoId() {
    }

    public Integer getIdSpostamento() {
        return idSpostamento;
    }

    public void setIdSpostamento(Integer idSpostamento) {
        this.idSpostamento = idSpostamento;
    }

    public Integer getIdPartecipante() {
        return idPartecipante;
    }

    public void setIdPartecipante(Integer idPartecipante) {
        this.idPartecipante = idPartecipante;
    }
}

