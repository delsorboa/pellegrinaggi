package it.pellegrinaggi.service;


import it.pellegrinaggi.model.ValoreTabella;

import it.pellegrinaggi.repository.SceltaRepository;
import it.pellegrinaggi.repository.ServizioRepository;
import it.pellegrinaggi.repository.PartecipanteRepository;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ValoreTabellaService {


    private final SceltaRepository sceltaRepository;

    private final ServizioRepository servizioRepository;

    private final PartecipanteRepository partecipanteRepository;


    public ValoreTabellaService(
            SceltaRepository sceltaRepository,
            ServizioRepository servizioRepository,
            PartecipanteRepository partecipanteRepository) {

        this.sceltaRepository = sceltaRepository;

        this.servizioRepository = servizioRepository;

        this.partecipanteRepository = partecipanteRepository;

    }


    public List<ValoreTabella> getValori(
            Integer idTabella,
            Integer idPellegrinaggio) {


        switch (idTabella) {


            case 1:

                return sceltaRepository.findAll()
                        .stream()
                        .map(scelta ->
                            new ValoreTabella(
                                scelta.getId(),
                                scelta.getDescrizione()
                            )
                        )
                        .toList();


            case 2:

                return servizioRepository
                        .findByIdPellegrinaggio(
                            idPellegrinaggio
                        )
                        .stream()
                        .map(servizio ->
                            new ValoreTabella(
                                servizio.getId(),
                                servizio.getDescrizione()
                            )
                        )
                        .toList();


            case 3:

                return partecipanteRepository.findAll()
                        .stream()
                        .map(partecipante ->
                            new ValoreTabella(
                                partecipante.getId(),
                                partecipante.getCognome()
                                    + " "
                                    + partecipante.getNome()
                            )
                        )
                        .toList();


            default:

                throw new RuntimeException(
                    "Tabella non gestita: "
                    + idTabella
                );

        }

    }

}
