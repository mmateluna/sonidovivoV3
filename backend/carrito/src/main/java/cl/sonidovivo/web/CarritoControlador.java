package cl.sonidovivo.web;

import cl.sonidovivo.dto.LineaCarrito;
import cl.sonidovivo.dto.PedidoRespuesta;
import cl.sonidovivo.dto.PedidoSolicitud;
import cl.sonidovivo.negocio.CarritoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@Tag(name = "Carrito", description = "Carrito y pedidos del usuario autenticado")
public class CarritoControlador {

  private final CarritoServicio servicio;

  public CarritoControlador(CarritoServicio servicio) {
    this.servicio = servicio;
  }

  @GetMapping("/carrito")
  @Operation(summary = "Lista el carrito del usuario")
  public List<LineaCarrito> listar(Authentication autenticacion) {
    return servicio.listar(usuario(autenticacion));
  }

  @PutMapping("/carrito")
  @Operation(summary = "Agrega o reemplaza la cantidad de un producto")
  public LineaCarrito guardar(@Valid @RequestBody LineaCarrito linea, Authentication autenticacion) {
    return servicio.guardar(usuario(autenticacion), linea);
  }

  @DeleteMapping("/carrito/items/{productoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Quita un producto del carrito")
  public void eliminar(@PathVariable long productoId, Authentication autenticacion) {
    servicio.eliminar(usuario(autenticacion), productoId);
  }

  @DeleteMapping("/carrito")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Vacía el carrito")
  public void vaciar(Authentication autenticacion) {
    servicio.vaciar(usuario(autenticacion));
  }

  @PostMapping("/pedidos")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Guarda el pedido y vacía el carrito. No cobra de verdad.")
  public PedidoRespuesta pedir(@Valid @RequestBody PedidoSolicitud solicitud, Authentication autenticacion) {
    return servicio.pedir(usuario(autenticacion), solicitud.metodo());
  }

  @GetMapping("/pedidos")
  @Operation(summary = "Lista los pedidos guardados del usuario")
  public List<PedidoRespuesta> pedidos(Authentication autenticacion) {
    return servicio.pedidos(usuario(autenticacion));
  }

  private long usuario(Authentication autenticacion) {
    if (autenticacion != null && autenticacion.getDetails() instanceof Long uid) {
      return uid;
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vuelve a iniciar sesión.");
  }
}
