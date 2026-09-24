package it.pellegrinaggi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tip_mezzo")
public class TipMezzo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String descrizione;

    public TipMezzo() {
    }

    public TipMezzo(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}

