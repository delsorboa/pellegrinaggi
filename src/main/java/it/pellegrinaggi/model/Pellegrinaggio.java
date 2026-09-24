package it.pellegrinaggi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="pellegrinaggio")
@Data
public class Pellegrinaggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_destinazione")
    private Destinazione destinazione;

    @Column(name = "data_partenza")
    private LocalDate dataPartenza;

    @Column(name = "data_ritorno")
    private LocalDate dataRitorno;

    private BigDecimal costo;

    private Integer postiTotali;
    
    private LocalDate dataDalSistemazione;
    
    private LocalDate dataAlSistemazione;
    
   
    public int getPostiLiberi(){

        return postiTotali - adesioni.size();

    }

    @Enumerated(EnumType.STRING)
    private StatoPellegrinaggio stato;
    
    @Column(length=1000)
    private String descrizione;
    
    @OneToMany(
            mappedBy = "pellegrinaggio",
            cascade = CascadeType.ALL
    )
    private List<Adesione> adesioni = new ArrayList<>();
}
