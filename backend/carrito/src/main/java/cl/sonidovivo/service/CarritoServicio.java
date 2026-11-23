package cl.sonidovivo.service;

import cl.sonidovivo.dto.LineaCarrito;
import cl.sonidovivo.modelo.CarritoItem;
import cl.sonidovivo.repository.CarritoItemRepositorio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarritoServicio {

  private final CarritoItemRepositorio items;

  public CarritoServicio(CarritoItemRepositorio items) {
    this.items = items;
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
