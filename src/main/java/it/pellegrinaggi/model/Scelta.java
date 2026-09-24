package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "scelta")
@Data
public class Scelta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(name = "id_pellegrinaggio", nullable = false)
//    private Integer idPellegrinaggio;

    @Column(nullable = false)
    private String descrizione;
}

