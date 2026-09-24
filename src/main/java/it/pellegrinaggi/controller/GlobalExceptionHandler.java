package it.pellegrinaggi.controller;


import it.pellegrinaggi.exception.AdesioneException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(AdesioneException.class)
    public String gestioneAdesione(
            AdesioneException e,
            Model model){


        model.addAttribute(
                "messaggio",
                e.getMessage()
        );


        return "errore";

    }


}