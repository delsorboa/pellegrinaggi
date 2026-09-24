package it.pellegrinaggi.security;


import it.pellegrinaggi.model.Utente;
import it.pellegrinaggi.repository.UtenteRepository;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;



@Component
public class CustomAuthenticationSuccessHandler 
        implements AuthenticationSuccessHandler {



    private final UtenteRepository repository;



    public CustomAuthenticationSuccessHandler(
            UtenteRepository repository){

        this.repository = repository;

    }




    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {



        Utente utente =
                repository.findByUsername(
                        authentication.getName()
                )
                .orElseThrow();



        // Primo accesso: cambio password obbligatorio

        if(Boolean.TRUE.equals(
                utente.getCambioPasswordObbligatorio())){


            response.sendRedirect(
                    "/cambio-password"
            );

            return;

        }




        // Amministratore

        if(authentication
                .getAuthorities()
                .stream()
                .anyMatch(
                    a -> a.getAuthority()
                    .equals("ROLE_ADMIN")
                )){


            response.sendRedirect(
                    "/admin"
            );


            return;

        }





        // Partecipante

        if(authentication
                .getAuthorities()
                .stream()
                .anyMatch(
                    a -> a.getAuthority()
                    .equals("ROLE_PARTECIPANTE")
                )){


            response.sendRedirect(
                    "/partecipante"
            );


            return;

        }



        response.sendRedirect("/login");


    }

}