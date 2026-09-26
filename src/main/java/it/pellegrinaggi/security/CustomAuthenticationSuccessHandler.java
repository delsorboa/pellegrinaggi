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
import java.util.Optional;

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

        if (response.isCommitted()) {
            return;
        }

        // 1. Gestione sicura del recupero utente per evitare NoSuchElementException
        Optional<Utente> utenteOpt = repository.findByUsername(authentication.getName());
        if (utenteOpt.isEmpty()) {
            System.out.println("ERRORE: Utente non trovato nel database dopo l'autenticazione: " + authentication.getName());
            response.sendRedirect(request.getContextPath() + "/login?error=usernotfound");
            return;
        }
        Utente utente = utenteOpt.get();

        // 2. Determina il percorso relativo di destinazione
        String targetPath = "/login";

        if (Boolean.TRUE.equals(utente.getCambioPasswordObbligatorio())) {
            targetPath = "/cambio-password";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            targetPath = "/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PARTECIPANTE"))) {
            targetPath = "/partecipante";
        }

        // 3. RICOSTRUZIONE URL PUBBLICO IN MODO ROBUSTO (Evita crash totali)
        String publicRedirectUrl = buildPublicUrl(request, targetPath);

        // 4. Esegue il redirect
        response.sendRedirect(publicRedirectUrl);
    }

    private String buildPublicUrl(HttpServletRequest request, String targetPath) {
        try {
            // Estrae l'host reale inoltrato da PandaStack/Cloudflare o dall'header standard
            String host = request.getHeader("X-Forwarded-Host");
            if (host == null || host.isEmpty()) {
                host = request.getHeader("Host");
            }

            if (host != null && !host.isEmpty()) {
                // Rimuove la porta in modo sicuro senza rompere il tipo String
                if (host.contains(":")) {
                    host = host.split(":")[0];
                }

                // Whitelist per verificare che l'host appartenga alla tua infrastruttura
                if (isAllowedHost(host)) {
                    // Gestione flessibile dello schema: mantiene http solo su localhost per i test
                    String scheme = host.equals("localhost") ? "http" : "https";
                    String portSuffix = host.equals("localhost") ? ":8080" : "";
                    
                    return scheme + "://" + host + portSuffix + targetPath;
                } else {
                    System.out.println("ATTENZIONE: Rilevato Host non autorizzato nella richiesta: " + host);
                }
            }
        } catch (Exception e) {
            System.out.println("Eccezione durante la build dell'URL: " + e.getMessage());
        }
        
        // Fallback assoluto e resiliente: se tutto fallisce, restituisce il percorso relativo
        // Evita che l'applicazione restituisca stringhe vuote o vada in crash
        return request.getContextPath() + targetPath;
    }

    private boolean isAllowedHost(String host) {
        if (host == null) return false;
        return host.equals("localhost") || 
               host.endsWith(".pandastack.ai") || 
               host.endsWith(".pellegrinaggi.it"); // Inserisci qui l'eventuale dominio di produzione definitivo
    }
}
