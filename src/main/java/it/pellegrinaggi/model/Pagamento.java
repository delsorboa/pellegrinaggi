package it.pellegrinaggi.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name="pagamento")
@Data
public class Pagamento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adesione_id")
    private Adesione adesione;


    private LocalDate dataPagamento;


    private BigDecimal importo;


    private String causale;


    private String note;
    
    private String statoPagamento = "INSERITO";
    
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;



    @PrePersist
    public void init(){

        if(dataPagamento == null){
            dataPagamento = LocalDate.now();
        }

    }

}