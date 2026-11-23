package cl.sonidovivo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

// Al borrar una cuenta, auth tiene que anular sus refresh tokens.
@Component
public class ClienteAuth {

  private final RestClient http;

  public ClienteAuth(
      @Value("${sonidovivo.auth.url}") String url,
      @Value("${sonidovivo.interno.secreto}") String secreto
  ) {
    this.http = RestClient.builder().baseUrl(url).defaultHeader("X-Interno", secreto).build();
  }

  public void revocar(long usuarioId) {
    try {
      http.post().uri("/api/interno/revocar/{id}", usuarioId).retrieve().toBodilessEntity();
    } catch (RestClientException ex) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo cerrar la sesión de esa cuenta.");
    }
  }
}
