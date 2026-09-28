package it.pellegrinaggi.repository;


import it.pellegrinaggi.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UtenteRepository 
extends JpaRepository<Utente,Integer>{


    Optional<Utente> findByUsername(
            String username
    );
    
    boolean existsByUsername(String username);
    
    
 // Trova se esiste già un utente legato a quel partecipanteId
    Optional<Utente> findByPartecipanteId(Integer partecipanteId);

    // Query personalizzata per recuperare il valore massimo dell'ID corrente
    @Query("SELECT COALESCE(MAX(u.id), 0) FROM Utente u")
    Integer findMaxId();

}