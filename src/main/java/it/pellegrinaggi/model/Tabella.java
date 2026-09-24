package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tabella")
@Data
public class Tabella {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descrizione;
}

