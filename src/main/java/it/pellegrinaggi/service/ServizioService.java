package it.pellegrinaggi.service;

import it.pellegrinaggi.model.Servizio;
import it.pellegrinaggi.repository.ServizioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServizioService {

    private final ServizioRepository servizioRepository;

    public ServizioService(ServizioRepository servizioRepository) {
        this.servizioRepository = servizioRepository;
    }


    public List<Servizio> findByPellegrinaggioId(Integer idPellegrinaggio) {

        return servizioRepository.findByIdPellegrinaggio(idPellegrinaggio);

    }


    public Servizio aggiungi(Integer idPellegrinaggio, String descrizione) {

        Servizio servizio = new Servizio();

        servizio.setIdPellegrinaggio(idPellegrinaggio);
        servizio.setDescrizione(descrizione);

        return servizioRepository.save(servizio);

    }


    public void elimina(Integer id) {

        servizioRepository.deleteById(id);

    }
}
