package it.pellegrinaggi.model;

public class ValoreTabella {

    private Integer id;
    private String descrizione;


    public ValoreTabella(
            Integer id,
            String descrizione) {

        this.id = id;
        this.descrizione = descrizione;

    }


    public Integer getId() {
        return id;
    }


    public String getDescrizione() {
        return descrizione;
    }

}
