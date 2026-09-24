package it.pellegrinaggi.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="adesione")
@Data
public class Adesione {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String stato;
    
    private BigDecimal  quota;



    @ManyToOne
    @JoinColumn(name="partecipante_id")
    private Partecipante partecipante;



    @ManyToOne
    @JoinColumn(name="pellegrinaggio_id")
    private Pellegrinaggio pellegrinaggio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = true)
    private Utente utente;
    
    @OneToMany(
            mappedBy = "adesione",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    	private List<Pagamento> pagamenti = new ArrayList<>();



    private LocalDateTime dataAdesione =
            LocalDateTime.now();


}