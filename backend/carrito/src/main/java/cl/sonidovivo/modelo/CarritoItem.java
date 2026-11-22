package cl.sonidovivo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// Copia del producto al momento de agregarlo. El catálogo está en otra base.
@Entity
@Table(name = "carrito_item", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
public class CarritoItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuario_id", nullable = false)
  private long usuarioId;

  @Column(name = "producto_id", nullable = false)
  private long productoId;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false)
  private int precio;

  @Column(length = 300)
  private String imagen;

  @Column(nullable = false)
  private int cantidad;

  @Column(nullable = false)
  private int stock;

  public Long getId() {
    return id;
  }

  public long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public long getProductoId() {
    return productoId;
  }

  public void setProductoId(long productoId) {
    this.productoId = productoId;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getPrecio() {
    return precio;
  }

  public void setPrecio(int precio) {
    this.precio = precio;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }
}
