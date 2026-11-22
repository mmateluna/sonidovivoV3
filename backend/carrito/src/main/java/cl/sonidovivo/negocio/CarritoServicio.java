package cl.sonidovivo.negocio;

import cl.sonidovivo.dto.LineaCarrito;
import cl.sonidovivo.dto.PedidoRespuesta;
import cl.sonidovivo.modelo.CarritoItem;
import cl.sonidovivo.modelo.Pedido;
import cl.sonidovivo.modelo.PedidoItem;
import cl.sonidovivo.repositorio.CarritoItemRepositorio;
import cl.sonidovivo.repositorio.PedidoRepositorio;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarritoServicio {

  private static final Map<String, String> METODOS = Map.of(
      "debito", "Tarjeta de débito",
      "credito", "Tarjeta de crédito",
      "transferencia", "Transferencia"
  );

  private final CarritoItemRepositorio items;
  private final PedidoRepositorio pedidos;

  public CarritoServicio(CarritoItemRepositorio items, PedidoRepositorio pedidos) {
    this.items = items;
    this.pedidos = pedidos;
  }

  public List<LineaCarrito> listar(long usuarioId) {
    return items.findByUsuarioId(usuarioId).stream().map(this::linea).toList();
  }

  @Transactional
  public LineaCarrito guardar(long usuarioId, LineaCarrito linea) {
    if (linea.stock() < 1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay stock de ese producto.");
    }
    int cantidad = Math.min(linea.cantidad(), linea.stock());
    CarritoItem item = items.findByUsuarioIdAndProductoId(usuarioId, linea.id()).orElseGet(CarritoItem::new);
    item.setUsuarioId(usuarioId);
    item.setProductoId(linea.id());
    item.setNombre(linea.nombre().trim());
    item.setPrecio(linea.precio());
    item.setImagen(linea.imagen() == null ? "" : linea.imagen().trim());
    item.setCantidad(cantidad);
    item.setStock(linea.stock());
    return linea(items.save(item));
  }

  @Transactional
  public void eliminar(long usuarioId, long productoId) {
    items.findByUsuarioIdAndProductoId(usuarioId, productoId).ifPresent(items::delete);
  }

  @Transactional
  public void vaciar(long usuarioId) {
    items.deleteByUsuarioId(usuarioId);
  }

  @Transactional
  public PedidoRespuesta pedir(long usuarioId, String metodo) {
    String etiqueta = METODOS.get(metodo);
    if (etiqueta == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elige un medio de pago.");
    }
    List<CarritoItem> lineas = items.findByUsuarioId(usuarioId);
    if (lineas.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay productos para pagar.");
    }
    int total = lineas.stream().mapToInt(linea -> linea.getPrecio() * linea.getCantidad()).sum();
    Pedido pedido = new Pedido();
    pedido.setUsuarioId(usuarioId);
    pedido.setMetodo(etiqueta);
    pedido.setTotal(total);
    pedido.setCreado(Instant.now());
    pedido = pedidos.save(pedido);
    for (CarritoItem linea : lineas) {
      PedidoItem item = new PedidoItem();
      item.setPedido(pedido);
      item.setProductoId(linea.getProductoId());
      item.setNombre(linea.getNombre());
      item.setPrecio(linea.getPrecio());
      item.setCantidad(linea.getCantidad());
      pedido.getItems().add(item);
    }
    pedidos.save(pedido);
    items.deleteByUsuarioId(usuarioId);
    return new PedidoRespuesta(pedido.getId(), etiqueta, total);
  }

  public List<PedidoRespuesta> pedidos(long usuarioId) {
    return pedidos.findByUsuarioIdOrderByIdDesc(usuarioId).stream()
        .map(pedido -> new PedidoRespuesta(pedido.getId(), pedido.getMetodo(), pedido.getTotal()))
        .toList();
  }

  private LineaCarrito linea(CarritoItem item) {
    return new LineaCarrito(
        item.getProductoId(),
        item.getNombre(),
        item.getPrecio(),
        item.getImagen(),
        item.getCantidad(),
        item.getStock()
    );
  }
}
