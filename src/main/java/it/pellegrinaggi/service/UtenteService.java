package it.pellegrinaggi.service;


import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;



@Service
public class UtenteService {


    private final UtenteRepository repository;



    public UtenteService(
            UtenteRepository repository){

        this.repository = repository;

    }




    public Utente getUtenteLoggato(
            Authentication authentication){


        return repository.findByUsername(
                authentication.getName()
        )
        .orElseThrow(
            () -> new RuntimeException(
                    "Utente non trovato"
            )
        );

    }


}