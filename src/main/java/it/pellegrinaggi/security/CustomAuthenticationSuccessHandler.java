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
import java.net.URI;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UtenteRepository repository;

    public CustomAuthenticationSuccessHandler(UtenteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        Utente utente = repository.findByUsername(authentication.getName()).orElseThrow();

        if (response.isCommitted()) {
            return;
        }

        // Determina il percorso relativo di destinazione
        String targetPath = "/login";

        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            targetPath = "/cambio-password";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            targetPath = "/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            targetPath = "/partecipante";
        }

        // RICOSTRUZIONE URL PUBBLICO PANDASTACK
        String publicRedirectUrl = buildPublicUrl(request, targetPath);

        // Esegue il redirect forzato sull'URL esterno accessibile
        response.sendRedirect(publicRedirectUrl);
    }

    private String buildPublicUrl(HttpServletRequest request, String targetPath) {
        try {
            // Proviamo a leggere l'origine o il referer inviato da PandaStack
            String originHeader = request.getHeader("origin");
            if (originHeader == null || originHeader.isEmpty()) {
                originHeader = request.getHeader("referer");
            }

            if (originHeader != null && !originHeader.isEmpty()) {
                URI uri = new URI(originHeader);
                // Estrae solo lo schema (forzando https) e l'host pubblico (es. xxxx.pandastack.ai)
                String scheme = "https"; 
                String host = uri.getHost();
                
                // Ricostruisce l'URL finale assoluto e sicuro per l'esterno
                return scheme + "://" + host + targetPath;
            }
        } catch (Exception e) {
            // Log di fallback in caso di errore di parsing dell'URI
            System.out.println("Errore nel parsing del dominio pubblico, uso il path relativo: " + e.getMessage());
        }
        
        // Fallback locale se gli header fossero assenti
        return targetPath;
    }
}
