package it.pellegrinaggi.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "responsabili_spostamento")
public class ResponsabileSpostamento {

    @EmbeddedId
    private ResponsabileSpostamentoId id;

    public ResponsabileSpostamento() {
    }

    public ResponsabileSpostamento(
            Integer idSpostamento,
            Integer idPartecipante) {

        this.id = new ResponsabileSpostamentoId();

        this.id.setIdSpostamento(idSpostamento);
        this.id.setIdPartecipante(idPartecipante);
    }

    public ResponsabileSpostamentoId getId() {
        return id;
    }

    public void setId(ResponsabileSpostamentoId id) {
        this.id = id;
    }
}

