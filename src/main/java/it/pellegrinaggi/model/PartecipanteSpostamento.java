package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name = "partecipante_spostamento")
@Data
public class PartecipanteSpostamento {

    @EmbeddedId
    private PartecipanteSpostamentoId id;

    // getter / setter
}

