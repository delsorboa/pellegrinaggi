package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name = "partecipante_evento")
@Data
public class PartecipanteEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_pellegrinaggio", nullable = false)
    private Integer idPellegrinaggio;

    @Column(name = "id_partecipante", nullable = false)
    private Integer idPartecipante;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Eventi evento;

    @ManyToOne
    @JoinColumn(name = "id_ruolo")
    private TipoRuolo ruolo;

    @ManyToOne
    @JoinColumn(name = "id_tabella")
    private Tabella tabella;
    
    @Column(name = "id_valore_tabella")
    private Integer idValoreTabella;

    // getter e setter
}

