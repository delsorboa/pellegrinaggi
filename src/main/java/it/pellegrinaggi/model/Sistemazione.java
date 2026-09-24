package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sistemazione")
@Data
public class Sistemazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_pellegrinaggio", nullable = false)
    private Integer idPellegrinaggio;

    @Column(nullable = false)
    private String descrizione;

    @OneToMany(
            mappedBy = "sistemazione",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Camera> camere = new ArrayList<>();
}
