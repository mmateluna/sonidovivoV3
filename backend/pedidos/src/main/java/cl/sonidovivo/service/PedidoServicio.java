package cl.sonidovivo.service;

import cl.sonidovivo.dto.LineaPedido;
import cl.sonidovivo.dto.PedidoRespuesta;
import cl.sonidovivo.modelo.Pedido;
import cl.sonidovivo.modelo.PedidoItem;
import cl.sonidovivo.repository.PedidoRepositorio;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoServicio {

  private final PedidoRepositorio pedidos;

  public PedidoServicio(PedidoRepositorio pedidos) {
    this.pedidos = pedidos;
  }

  @Transactional
  public PedidoRespuesta crear(long usuarioId, List<LineaPedido> lineas) {
    if (lineas == null || lineas.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay productos para pagar.");
    }
    Pedido pedido = new Pedido();
    pedido.setUsuarioId(usuarioId);
    pedido.setTotal(0);
    pedido.setCreado(Instant.now());
    pedido = pedidos.save(pedido);

    int total = 0;
    for (LineaPedido linea : lineas) {
      total += linea.precio() * linea.cantidad();
      PedidoItem item = new PedidoItem();
      item.setPedido(pedido);
      item.setProductoId(linea.productoId());
      item.setNombre(linea.nombre().trim());
      item.setPrecio(linea.precio());
      item.setCantidad(linea.cantidad());
      pedido.getItems().add(item);
    }
    pedido.setTotal(total);
    pedidos.save(pedido);
    return new PedidoRespuesta(pedido.getId(), total);
  }

  public List<PedidoRespuesta> listar(long usuarioId) {
    return pedidos.findByUsuarioIdOrderByIdDesc(usuarioId).stream()
        .map(pedido -> new PedidoRespuesta(pedido.getId(), pedido.getTotal()))
        .toList();
  }
}
