package cl.sonidovivo.controller;

import cl.sonidovivo.dto.PedidoRespuesta;
import cl.sonidovivo.service.PedidoServicio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoConsultaControlador {

  private final PedidoServicio servicio;

  public PedidoConsultaControlador(PedidoServicio servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  public List<PedidoRespuesta> listar(Authentication autenticacion) {
    if (autenticacion == null || !(autenticacion.getDetails() instanceof Long uid)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vuelve a iniciar sesión.");
    }
    return servicio.listar(uid);
  }
}
