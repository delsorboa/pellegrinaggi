package it.pellegrinaggi.service;


import it.pellegrinaggi.repository.*;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;



@Service
public class DashboardService {


private final PagamentoRepository pagamentoRepository;

private final AdesioneRepository adesioneRepository;



public DashboardService(
        PagamentoRepository pagamentoRepository,
        AdesioneRepository adesioneRepository){

    this.pagamentoRepository =
            pagamentoRepository;

    this.adesioneRepository =
            adesioneRepository;
}



public BigDecimal totaleIncassato(){

    return pagamentoRepository
            .sommaPagamenti();

}



}
