package it.pellegrinaggi.repository;


import it.pellegrinaggi.model.Utente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    
    // Esegue un JOIN FETCH per ottimizzare le prestazioni nella tabella
    @Query(value = "SELECT u FROM Utente u LEFT JOIN FETCH u.partecipante",
           countQuery = "SELECT count(u) FROM Utente u")
    Page<Utente> findAll(Pageable pageable);

    Page<Utente> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

}