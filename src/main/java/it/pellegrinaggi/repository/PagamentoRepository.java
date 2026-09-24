package it.pellegrinaggi.repository;


import it.pellegrinaggi.model.Adesione;
import it.pellegrinaggi.model.Pagamento;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PagamentoRepository 
extends JpaRepository<Pagamento,Integer>{
	
	@Query("""
			SELECT COALESCE(
			SUM(p.importo),0
			)
			FROM Pagamento p
			""")
			BigDecimal sommaPagamenti();
	
    List<Pagamento> findByAdesione(
            Adesione adesione
    );
    
    @Query("""
    		SELECT COALESCE(
    		SUM(p.importo),0
    		)
    		FROM Pagamento p
    		WHERE p.adesione = :adesione
    		AND p.statoPagamento = 'CONFERMATO'
    		""")
    		BigDecimal totalePagato(
    		        @Param("adesione") Adesione adesione
    		);
    
    
    

}