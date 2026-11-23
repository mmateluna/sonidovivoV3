package cl.sonidovivo.service;

import cl.sonidovivo.dto.LoginSolicitud;
import cl.sonidovivo.dto.PerfilInterno;
import cl.sonidovivo.dto.RegistroSolicitud;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

// Auth no guarda usuarios. Le pregunta a ese servicio si el correo y la clave calzan.
@Component
public class ClienteUsuarios {

  private final RestClient http;
  private final ObjectMapper json = new ObjectMapper();

  public ClienteUsuarios(
      @Value("${sonidovivo.usuarios.url}") String url,
      @Value("${sonidovivo.interno.secreto}") String secreto
  ) {
    this.http = RestClient.builder().baseUrl(url).defaultHeader("X-Interno", secreto).build();
  }

  public PerfilInterno validar(String correo, String clave) {
    return pedir(
        () -> http.post().uri("/api/interno/validar").body(new LoginSolicitud(correo, clave)).retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> fallar(res.getStatusCode(), res.getBody()))
            .body(PerfilInterno.class),
        "El servicio de usuarios no responde."
    );
  }

  public PerfilInterno registrar(RegistroSolicitud datos) {
    return pedir(
        () -> http.post().uri("/api/interno/registro").body(datos).retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> fallar(res.getStatusCode(), res.getBody()))
            .body(PerfilInterno.class),
        "El servicio de usuarios no responde."
    );
  }

  public PerfilInterno porId(long id) {
    return pedir(
        () -> http.get().uri("/api/interno/usuarios/{id}", id).retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> fallar(res.getStatusCode(), res.getBody()))
            .body(PerfilInterno.class),
        "El servicio de usuarios no responde."
    );
  }

  public PerfilInterno porCorreo(String correo) {
    return pedir(
        () -> http.get().uri(uri -> uri.path("/api/interno/correo").queryParam("correo", correo).build()).retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> fallar(res.getStatusCode(), res.getBody()))
            .body(PerfilInterno.class),
        "El servicio de usuarios no responde."
    );
  }

  private PerfilInterno pedir(Supplier<PerfilInterno> llamada, String caido) {
    try {
      PerfilInterno perfil = llamada.get();
      if (perfil == null || perfil.id() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, caido);
      }
      return perfil;
    } catch (ResponseStatusException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, caido);
    }
  }

  private void fallar(HttpStatusCode estado, java.io.InputStream cuerpo) throws IOException {
    String mensaje = "No se pudo completar la operación.";
    try {
      JsonNode nodo = json.readTree(cuerpo);
      if (nodo != null && nodo.hasNonNull("mensaje")) {
        mensaje = nodo.get("mensaje").asText();
      }
    } catch (Exception ignorado) {
      // El otro servicio no devolvió JSON.
    }
    throw new ResponseStatusException(HttpStatus.valueOf(estado.value()), mensaje);
  }
}
