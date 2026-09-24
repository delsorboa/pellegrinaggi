package it.pellegrinaggi.service;


import it.pellegrinaggi.model.*;
import it.pellegrinaggi.repository.UtenteRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class AccountService {


    private final UtenteRepository repository;

    private final PasswordEncoder encoder;



    public AccountService(
            UtenteRepository repository,
            PasswordEncoder encoder){

        this.repository = repository;
        this.encoder = encoder;

    }



    public String creaAccount(
            Partecipante partecipante){


        String username =
        		  generaUsername(partecipante);



        String passwordTemporanea =
        		generaPassword();



        Utente u = new Utente();

        u.setUsername(username);

        u.setPassword(
                encoder.encode(passwordTemporanea)
        );

        u.setRuolo(
                Ruolo.PARTECIPANTE
        );

        u.setAttivo(true);

        u.setCambioPasswordObbligatorio(true);

        u.setPartecipante(partecipante);


        repository.save(u);


        return passwordTemporanea;

    }
    
    private String generaUsername(
            Partecipante p) {


        String base =
                p.getNome()
                .toLowerCase()
                .trim()
                + "."
                + p.getCognome()
                .toLowerCase()
                .trim();


        String username = base;

        int numero = 1;


        while(repository.existsByUsername(username)) {

            username = base + numero;

            numero++;

        }


        return username;

    }
    
    private String generaPassword() {

        return UUID.randomUUID()
                .toString()
                .substring(0,8);

    }


}