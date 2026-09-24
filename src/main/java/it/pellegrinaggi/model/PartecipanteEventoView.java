package it.pellegrinaggi.model;

import lombok.Data;

@Data
public class PartecipanteEventoView {

    private Integer id;

    private String descrizioneEvento;

    private String descrizioneRuolo;

    private String descrizioneValore;
    
    private Integer idValoreTabella;

	public PartecipanteEventoView(Integer id, String descrizioneEvento, String descrizioneRuolo,
			String descrizioneValore, Integer idValoreTabella) {
		super();
		this.id = id;
		this.descrizioneEvento = descrizioneEvento;
		this.descrizioneRuolo = descrizioneRuolo;
		this.descrizioneValore = descrizioneValore;
		this.idValoreTabella = idValoreTabella;
	}


  
}

