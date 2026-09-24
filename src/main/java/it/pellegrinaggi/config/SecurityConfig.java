package it.pellegrinaggi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import it.pellegrinaggi.security.CustomAuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	   private final CustomAuthenticationSuccessHandler successHandler;



	    public SecurityConfig(
	        CustomAuthenticationSuccessHandler successHandler){

	        this.successHandler = successHandler;

	    }


    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                            "/login",
                            "/registrazione",
                            "/css/**",
                            "/js/**",
                            "/images/**"
                    ).permitAll()

                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    .requestMatchers("/utente/**")
                    .hasAnyRole("ADMIN", "PARTECIPANTE")
                    
                    .requestMatchers("/partecipante/**")
                    .hasRole("PARTECIPANTE")
                    
                    .requestMatchers("/cambio-password")
                    .authenticated()

                    .anyRequest()
                    .authenticated()

            )

            .formLogin(login -> login

                    .loginPage("/login")

                    .loginProcessingUrl("/login")

                    .successHandler(
                    		successHandler
                        )

                    .failureUrl("/login?error")

                    .permitAll()

            )

            .logout(logout -> logout

                    .logoutUrl("/logout")

                    .logoutSuccessUrl("/login?logout")

                    .permitAll()

            );

        return http.build();
    }

}