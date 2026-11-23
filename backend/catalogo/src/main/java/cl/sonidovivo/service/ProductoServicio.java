package cl.sonidovivo.service;

import cl.sonidovivo.dto.ProductoSolicitud;
import cl.sonidovivo.modelo.Producto;
import cl.sonidovivo.repository.ProductoRepositorio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoServicio {

  private final ProductoRepositorio productos;

  public ProductoServicio(ProductoRepositorio productos) {
    this.productos = productos;
  }

  public List<Producto> listar() {
    return productos.findAll();
  }

  public Producto obtener(Long id) {
    return productos.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado.")
    );
  }

  @Transactional
  public Producto crear(ProductoSolicitud datos) {
    String codigo = datos.codigo().trim();
    if (productos.findByCodigo(codigo).isPresent()) {
      throw new ReglaNegocioException("Ya existe un producto con ese código.");
    }
    Producto producto = new Producto();
    copiar(producto, datos, codigo);
    return productos.save(producto);
  }

  @Transactional
  public Producto actualizar(Long id, ProductoSolicitud datos) {
    Producto producto = obtener(id);
    String codigo = datos.codigo().trim();
    productos.findByCodigo(codigo)
        .filter(otro -> !otro.getId().equals(id))
        .ifPresent(otro -> {
          throw new ReglaNegocioException("Ya existe un producto con ese código.");
        });
    copiar(producto, datos, codigo);
    return productos.save(producto);
  }

  @Transactional
  public void eliminar(Long id) {
    if (!productos.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado.");
    }
    productos.deleteById(id);
  }

  private void copiar(Producto producto, ProductoSolicitud datos, String codigo) {
    producto.setCodigo(codigo);
    producto.setNombre(datos.nombre().trim());
    producto.setMarca(texto(datos.marca()));
    producto.setModelo(texto(datos.modelo()));
    producto.setPrecio(datos.precio());
    producto.setCategoria(datos.categoria().trim());
    producto.setStock(datos.stock());
    producto.setStockCritico(datos.stockCritico() == null ? 0 : datos.stockCritico());
    producto.setImagen(texto(datos.imagen()));
    producto.setDescripcion(texto(datos.descripcion()));
  }

  private String texto(String valor) {
    return valor == null ? "" : valor.trim();
  }
}
