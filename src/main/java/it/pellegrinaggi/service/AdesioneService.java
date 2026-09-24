package it.pellegrinaggi.service;


import it.pellegrinaggi.exception.AdesioneException;
import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.AdesioneRepository;
import it.pellegrinaggi.repository.PartecipanteRepository;
import it.pellegrinaggi.repository.PartecipanteSpostamentoRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Service
public class AdesioneService {


    private final AdesioneRepository repository;
    private final PartecipanteSpostamentoRepository partecipanteSpostamentoRepository;



    public AdesioneService(
            AdesioneRepository repository,
            PartecipanteSpostamentoRepository partecipanteSpostamentoRepository){

        this.repository = repository;
        this.partecipanteSpostamentoRepository = partecipanteSpostamentoRepository;

    }



    public void iscrivi(
            Partecipante partecipante,
            Pellegrinaggio pellegrinaggio,
            Utente utente){



        if(
            pellegrinaggio.getStato()
            != StatoPellegrinaggio.APERTO
        ){

            throw new RuntimeException(
                    "Pellegrinaggio non disponibile"
            );

        }



        if(
          repository.existsByPartecipanteAndPellegrinaggio(
              partecipante,
              pellegrinaggio)
        ){

        	throw new AdesioneException(
        	        "Hai già effettuato l'adesione a questo pellegrinaggio"
        	);

        }



        long iscritti =
                repository.countByPellegrinaggio(
                        pellegrinaggio
                );


        if(iscritti >= pellegrinaggio.getPostiTotali()) {

            throw new RuntimeException(
                "Posti esauriti"
            );

        }



        Adesione adesione =
                new Adesione();


        adesione.setPartecipante(
                partecipante
        );


        adesione.setPellegrinaggio(
                pellegrinaggio
        );
        
        adesione.setUtente(
        		utente
        );
        
        adesione.setQuota(pellegrinaggio.getCosto());


        repository.save(adesione);

    }
    
    
    public void creaAdesioneAdmin(
            Partecipante partecipante,
            Pellegrinaggio pellegrinaggio,
            BigDecimal quota,
            String stato
    ) {
    	
    	  if(
    		        repository.existsByPartecipanteAndPellegrinaggio(
    		                partecipante,
    		                pellegrinaggio)
    		    ){

    		        throw new AdesioneException(
    		            "Il partecipante risulta già iscritto a questo pellegrinaggio"
    		        );

    		    }
    	  
    	Adesione adesione = new Adesione();

    	adesione.setPartecipante(partecipante);
    	adesione.setPellegrinaggio(pellegrinaggio);
    	adesione.setQuota(quota);
    	adesione.setStato("INSERITA");

    	repository.save(adesione);
    }
    
    
    public Adesione findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Adesione non trovata: " + id
                    )
                );
    }
    
    @Transactional
    public void associaSpostamento(Integer idAdesione, Integer idSpostamento) {

        Adesione adesione = repository.findById(idAdesione)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Adesione non trovata: " + idAdesione
                        )
                );

        Integer idPartecipante = adesione.getPartecipante().getId();

        boolean giaAssociato =
                partecipanteSpostamentoRepository
                        .existsByIdIdPartecipanteAndIdIdSpostamento(
                                idPartecipante,
                                idSpostamento
                        );

        if (giaAssociato) {
            throw new IllegalStateException(
                    "Lo spostamento è già associato al partecipante"
            );
        }

        PartecipanteSpostamentoId relazioneId =
                new PartecipanteSpostamentoId();

        relazioneId.setIdPartecipante(idPartecipante);
        relazioneId.setIdSpostamento(idSpostamento);

        PartecipanteSpostamento relazione =
                new PartecipanteSpostamento();

        relazione.setId(relazioneId);

        partecipanteSpostamentoRepository.save(relazione);
    }

    
    @Transactional
    public void rimuoviSpostamento(
            Integer idAdesione,
            Integer idSpostamento) {

        Adesione adesione = repository.findById(idAdesione)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Adesione non trovata: " + idAdesione
                        )
                );

        Integer idPartecipante = adesione.getPartecipante().getId();

        partecipanteSpostamentoRepository
                .deleteByIdIdPartecipanteAndIdIdSpostamento(
                        idPartecipante,
                        idSpostamento
                );
    }




}
