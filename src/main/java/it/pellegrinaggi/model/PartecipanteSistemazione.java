package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "partecipante_sistemazione",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"id_partecipante", "id_camera"}
                )
        }
)
@Data
public class PartecipanteSistemazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_partecipante", nullable = false)
    private Partecipante partecipante;

    @ManyToOne
    @JoinColumn(name = "id_camera", nullable = false)
    private Camera camera;
}

