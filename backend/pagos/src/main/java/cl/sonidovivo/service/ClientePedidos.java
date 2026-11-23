package cl.sonidovivo.service;

import cl.sonidovivo.dto.LineaRemota;
import cl.sonidovivo.dto.PedidoCreado;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ClientePedidos {

  private final RestClient http;
  private final ObjectMapper json = new ObjectMapper();

  public ClientePedidos(
      @Value("${sonidovivo.pedidos.url}") String url,
      @Value("${sonidovivo.interno.secreto}") String secreto
  ) {
    this.http = RestClient.builder().baseUrl(url).defaultHeader("X-Interno", secreto).build();
  }

  public PedidoCreado crear(long usuarioId, List<LineaRemota> lineas) {
    List<Map<String, Object>> items = lineas.stream()
        .map(linea -> Map.<String, Object>of(
            "productoId", linea.id(),
            "nombre", linea.nombre(),
            "precio", linea.precio(),
            "cantidad", linea.cantidad()
        ))
        .toList();
    try {
      PedidoCreado creado = http.post()
          .uri("/api/interno/pedidos")
          .body(Map.of("usuarioId", usuarioId, "lineas", items))
          .retrieve()
          .onStatus(HttpStatusCode::isError, (req, res) -> {
            throw new ResponseStatusException(HttpStatus.valueOf(res.getStatusCode().value()), mensaje(res.getBody()));
          })
          .body(PedidoCreado.class);
      if (creado == null || creado.id() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El servicio de pedidos no responde.");
      }
      return creado;
    } catch (ResponseStatusException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de pedidos no responde.");
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
