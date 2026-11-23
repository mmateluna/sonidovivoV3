package cl.sonidovivo.controller;

import cl.sonidovivo.service.AuthServicio;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Lo llama el servicio de usuarios cuando un administrador borra una cuenta.
@RestController
@RequestMapping("/api/interno")
public class InternoControlador {

  private final AuthServicio servicio;
  private final String secreto;

  public InternoControlador(AuthServicio servicio, @Value("${sonidovivo.interno.secreto}") String secreto) {
    this.servicio = servicio;
    this.secreto = secreto;
  }

  @PostMapping("/revocar/{usuarioId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void revocar(@PathVariable long usuarioId, HttpServletRequest request) {
    if (!secreto.equals(request.getHeader("X-Interno"))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción.");
    }
    servicio.revocar(usuarioId);
  }
}
