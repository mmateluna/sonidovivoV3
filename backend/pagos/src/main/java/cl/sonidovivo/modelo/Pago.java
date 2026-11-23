package cl.sonidovivo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

// El cobro es simulado: se guarda el medio y el monto, sin cargo real.
@Entity
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuario_id", nullable = false)
  private long usuarioId;

  @Column(name = "pedido_id", nullable = false)
  private long pedidoId;

  @Column(nullable = false, length = 40)
  private String metodo;

  @Column(nullable = false)
  private int total;

  @Column(nullable = false)
  private Instant creado;

  public Long getId() {
    return id;
  }

  public long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public long getPedidoId() {
    return pedidoId;
  }

  public void setPedidoId(long pedidoId) {
    this.pedidoId = pedidoId;
  }

  public String getMetodo() {
    return metodo;
  }

  public void setMetodo(String metodo) {
    this.metodo = metodo;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public Instant getCreado() {
    return creado;
  }

  public void setCreado(Instant creado) {
    this.creado = creado;
  }
}
