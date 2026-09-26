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
            String originHeader = request.getHeader("origin");
            if (originHeader == null || originHeader.isEmpty()) {
                originHeader = request.getHeader("referer");
            }

            if (originHeader != null && !originHeader.isEmpty()) {
                URI uri = new URI(originHeader);
                String host = uri.getHost();
                
                // --- PROTEZIONE: VALIDA L'HOST PRIMA DI PROCEDERE ---
                if (host != null && isAllowedHost(host)) {
                    String scheme = "https"; 
                    return scheme + "://" + host + targetPath;
                } else {
                    // Log di avviso per potenziale attacco o configurazione errata
                    System.out.println("Tentativo di redirect non autorizzato verso l'host: " + host);
                }
            }
        } catch (Exception e) {
            System.out.println("Errore nel parsing del dominio pubblico: " + e.getMessage());
        }
        
        // Fallback sicuro sul path relativo
        return targetPath;
    }

    // Metodo di validazione dell'host
    private boolean isAllowedHost(String host) {
        // Sostituisci con il tuo reale dominio o pattern
        return host.equals("localhost") || 
               host.equals("I_TUOI_PROVVEDIMENTI.pandastack.ai") || 
               host.endsWith(".tuodominio.it");
    }

}
