package it.pellegrinaggi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name="partecipante")
@Data
public class Partecipante {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String nome;


    private String cognome;


    private LocalDate dataNascita;


    private String telefono;


    private String email;


    private String codiceFiscale;


    private String indirizzo;
    
    private String citta;
    
    private String cap;
    
    private Integer idQualifica;
    
    private Integer idGrado;
    


    @OneToMany(
            mappedBy = "partecipante",
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Adesione> adesioni;
    
    
    @OneToOne(mappedBy = "partecipante",
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
  private Utente utente;
    
    


}
