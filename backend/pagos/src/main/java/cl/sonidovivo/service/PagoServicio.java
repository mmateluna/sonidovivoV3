package cl.sonidovivo.service;

import cl.sonidovivo.dto.LineaRemota;
import cl.sonidovivo.dto.PagoRespuesta;
import cl.sonidovivo.dto.PedidoCreado;
import cl.sonidovivo.modelo.Pago;
import cl.sonidovivo.repository.PagoRepositorio;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PagoServicio {

  private static final Map<String, String> METODOS = Map.of(
      "debito", "Tarjeta de débito",
      "credito", "Tarjeta de crédito",
      "transferencia", "Transferencia"
  );

  private final PagoRepositorio pagos;
  private final ClienteCarrito carrito;
  private final ClientePedidos pedidos;

  public PagoServicio(PagoRepositorio pagos, ClienteCarrito carrito, ClientePedidos pedidos) {
    this.pagos = pagos;
    this.carrito = carrito;
    this.pedidos = pedidos;
  }

  @Transactional
  public PagoRespuesta cobrar(long usuarioId, String metodo, String autorizacion) {
    String etiqueta = METODOS.get(metodo);
    if (etiqueta == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elige un medio de pago.");
    }
    List<LineaRemota> lineas = carrito.listar(autorizacion);
    if (lineas.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay productos para pagar.");
    }
    PedidoCreado pedido = pedidos.crear(usuarioId, lineas);
    Pago pago = new Pago();
    pago.setUsuarioId(usuarioId);
    pago.setPedidoId(pedido.id());
    pago.setMetodo(etiqueta);
    pago.setTotal(pedido.total());
    pago.setCreado(Instant.now());
    pagos.save(pago);
    carrito.vaciar(autorizacion);
    return new PagoRespuesta(pedido.id(), etiqueta, pedido.total());
  }
}
