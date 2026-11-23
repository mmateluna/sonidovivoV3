package cl.sonidovivo.seguridad;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

  private final FiltroJwt filtroJwt;

  public SeguridadConfig(FiltroJwt filtroJwt) {
    this.filtroJwt = filtroJwt;
  }

  @Bean
  public SecurityFilterChain filtro(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/api/pagos").authenticated()
            .anyRequest().authenticated()
        )
        .exceptionHandling(errores -> errores
            .authenticationEntryPoint((request, response, ex) -> {
              response.setStatus(401);
              response.setContentType("application/json");
              response.getWriter().write("{\"mensaje\":\"Debes iniciar sesión.\"}");
            })
            .accessDeniedHandler((request, response, ex) -> {
              response.setStatus(403);
              response.setContentType("application/json");
              response.getWriter().write("{\"mensaje\":\"No tienes permiso para esta acción.\"}");
            })
        )
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(List.of(
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:4173",
        "http://127.0.0.1:4173"
    ));
    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
