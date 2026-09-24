package it.pellegrinaggi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
public class Spostamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_pellegrinaggio", nullable = false)
    private Pellegrinaggio pellegrinaggio;

    @ManyToOne
    @JoinColumn(name = "id_mezzo", nullable = false)
    private TipMezzo tipMezzo;

    @ManyToOne
    @JoinColumn(name = "id_viaggio", nullable = false)
    private TipViaggio tipViaggio;

    @Column(name = "data_partenza", nullable = false)
    private LocalDate dataPartenza;

    @Column(name = "data_arrivo", nullable = false)
    private LocalDate dataArrivo;

    @Column(name = "luogo_partenza", nullable = false)
    private String luogoPartenza;

    @Column(name = "luogo_arrivo", nullable = false)
    private String luogoArrivo;

    @Column(length = 2000)
    private String note;

    public Spostamento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pellegrinaggio getPellegrinaggio() {
        return pellegrinaggio;
    }

    public void setPellegrinaggio(Pellegrinaggio pellegrinaggio) {
        this.pellegrinaggio = pellegrinaggio;
    }

    public TipMezzo getTipMezzo() {
        return tipMezzo;
    }

    public void setTipMezzo(TipMezzo tipMezzo) {
        this.tipMezzo = tipMezzo;
    }

    public TipViaggio getTipViaggio() {
        return tipViaggio;
    }

    public void setTipViaggio(TipViaggio tipViaggio) {
        this.tipViaggio = tipViaggio;
    }

    public LocalDate getDataPartenza() {
        return dataPartenza;
    }

    public void setDataPartenza(LocalDate dataPartenza) {
        this.dataPartenza = dataPartenza;
    }

    public LocalDate getDataArrivo() {
        return dataArrivo;
    }

    public void setDataArrivo(LocalDate dataArrivo) {
        this.dataArrivo = dataArrivo;
    }

    public String getLuogoPartenza() {
        return luogoPartenza;
    }

    public void setLuogoPartenza(String luogoPartenza) {
        this.luogoPartenza = luogoPartenza;
    }

    public String getLuogoArrivo() {
        return luogoArrivo;
    }

    public void setLuogoArrivo(String luogoArrivo) {
        this.luogoArrivo = luogoArrivo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
