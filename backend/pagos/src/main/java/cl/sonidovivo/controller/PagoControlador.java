package cl.sonidovivo.controller;

import cl.sonidovivo.dto.PagoRespuesta;
import cl.sonidovivo.dto.PagoSolicitud;
import cl.sonidovivo.service.PagoServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pagos")
public class PagoControlador {

  private final PagoServicio servicio;

  public PagoControlador(PagoServicio servicio) {
    this.servicio = servicio;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PagoRespuesta cobrar(
      @Valid @RequestBody PagoSolicitud solicitud,
      @RequestHeader("Authorization") String autorizacion,
      Authentication autenticacion
  ) {
    if (autenticacion == null || !(autenticacion.getDetails() instanceof Long uid)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vuelve a iniciar sesión.");
    }
    return servicio.cobrar(uid, solicitud.metodo(), autorizacion);
  }
}
