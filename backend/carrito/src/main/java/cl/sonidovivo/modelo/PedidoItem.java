package cl.sonidovivo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido_item")
public class PedidoItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "pedido_id")
  private Pedido pedido;

  @Column(name = "producto_id", nullable = false)
  private long productoId;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false)
  private int precio;

  @Column(nullable = false)
  private int cantidad;

  public void setPedido(Pedido pedido) {
    this.pedido = pedido;
  }

  public void setProductoId(long productoId) {
    this.productoId = productoId;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setPrecio(int precio) {
    this.precio = precio;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }
}
