package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "eventi")
@Data
public class Eventi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descrizione;

    private String note;

    @Column(name = "id_pellegrinaggio", nullable = false)
    private Integer idPellegrinaggio;
    
    
    @ManyToOne
    @JoinColumn(name = "id_tipo_evento")
    private TipoEvento tipoEvento;

    @ManyToOne
    @JoinColumn(name = "id_tabella")
    private Tabella tabella;
}

