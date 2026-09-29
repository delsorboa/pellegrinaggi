package it.pellegrinaggi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.pellegrinaggi.model.Partecipante;

public interface PartecipanteRepository extends JpaRepository<Partecipante, Integer> {
    
    boolean existsByCodiceFiscaleAndIdNot(String codiceFiscale, Integer id);
    
    Optional<Partecipante> findById(Integer id);
    
    List<Partecipante> findByAdesioniPellegrinaggioId(Integer idPellegrinaggio);
     
    @Query("""
        SELECT DISTINCT p
        FROM Partecipante p
        JOIN p.adesioni a
        WHERE a.pellegrinaggio.id = :idPellegrinaggio
          AND p.id <> :idPartecipante
    """)
    List<Partecipante> findByAdesioniPellegrinaggioIdExcludingPartecipante(
            @Param("idPellegrinaggio") Integer idPellegrinaggio,
            @Param("idPartecipante") Integer idPartecipante
    );
     
    // ========================================================
    // METODI DI RICERCA CON PAGINAZIONE CORRETTI (Aggiunto Pageable)
    // ========================================================

    // 1. Ricerca solo per Nome (parziale e case-insensitive)
    Page<Partecipante> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // 2. Ricerca solo per Cognome (parziale e case-insensitive)
    Page<Partecipante> findByCognomeContainingIgnoreCase(String cognome, Pageable pageable);

    // 3. Ricerca combinata Nome E Cognome (entrambi parziali e case-insensitive)
    Page<Partecipante> findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCase(String nome, String cognome, Pageable pageable);
}
