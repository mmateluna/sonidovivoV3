package cl.sonidovivo.controller;

import cl.sonidovivo.dto.PedidoInterno;
import cl.sonidovivo.dto.PedidoRespuesta;
import cl.sonidovivo.service.PedidoServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// El navegador no crea pedidos directo. Lo hace el servicio de pagos.
@RestController
@RequestMapping("/api/interno/pedidos")
public class PedidoControlador {

  private final PedidoServicio servicio;
  private final String secreto;

  public PedidoControlador(PedidoServicio servicio, @Value("${sonidovivo.interno.secreto}") String secreto) {
    this.servicio = servicio;
    this.secreto = secreto;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PedidoRespuesta crear(@Valid @RequestBody PedidoInterno datos, HttpServletRequest request) {
    if (!secreto.equals(request.getHeader("X-Interno"))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción.");
    }
    return servicio.crear(datos.usuarioId(), datos.lineas());
  }
}
