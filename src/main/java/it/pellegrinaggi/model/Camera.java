package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "camera")
@Data
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_sistemazione", nullable = false)
    private Sistemazione sistemazione;

    @Column(nullable = false)
    private String descrizione;

    @OneToMany(
            mappedBy = "camera",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PartecipanteSistemazione> partecipanti = new ArrayList<>();
}

