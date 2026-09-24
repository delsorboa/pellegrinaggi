package it.pellegrinaggi.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
@Table(name="utente")
@Data
public class Utente {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(unique = true)
    private String username;


    private String password;


    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;


    private Boolean attivo = true;


    private Boolean cambioPasswordObbligatorio = true;



    @OneToOne
    @JoinColumn(name="partecipante_id")
    private Partecipante partecipante;
    
    @OneToMany(mappedBy = "utente")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Adesione> adesioni = new ArrayList<>();

}