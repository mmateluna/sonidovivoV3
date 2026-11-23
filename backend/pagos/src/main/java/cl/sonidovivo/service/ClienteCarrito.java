package cl.sonidovivo.service;

import cl.sonidovivo.dto.LineaRemota;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ClienteCarrito {

  private final RestClient http;
  private final ObjectMapper json = new ObjectMapper();

  public ClienteCarrito(@Value("${sonidovivo.carrito.url}") String url) {
    this.http = RestClient.builder().baseUrl(url).build();
  }

  public List<LineaRemota> listar(String autorizacion) {
    try {
      LineaRemota[] lineas = http.get()
          .uri("/api/carrito")
          .header("Authorization", autorizacion)
          .retrieve()
          .onStatus(HttpStatusCode::isError, (req, res) -> {
            throw new ResponseStatusException(HttpStatus.valueOf(res.getStatusCode().value()), mensaje(res.getBody()));
          })
          .body(LineaRemota[].class);
      return lineas == null ? List.of() : Arrays.asList(lineas);
    } catch (ResponseStatusException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de carrito no responde.");
    }
  }

  public void vaciar(String autorizacion) {
    try {
      http.delete()
          .uri("/api/carrito")
          .header("Authorization", autorizacion)
          .retrieve()
          .onStatus(HttpStatusCode::isError, (req, res) -> {
            throw new ResponseStatusException(HttpStatus.valueOf(res.getStatusCode().value()), mensaje(res.getBody()));
          })
          .toBodilessEntity();
    } catch (ResponseStatusException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de carrito no responde.");
    }
  }

  private String mensaje(java.io.InputStream cuerpo) {
    try {
      JsonNode nodo = json.readTree(cuerpo);
      if (nodo != null && nodo.hasNonNull("mensaje")) {
        return nodo.get("mensaje").asText();
      }
    } catch (Exception ignorado) {
      // Sin JSON.
    }
    return "No se pudo completar la operación.";
  }
}
