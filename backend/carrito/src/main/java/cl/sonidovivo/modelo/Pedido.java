package cl.sonidovivo.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuario_id", nullable = false)
  private long usuarioId;

  @Column(nullable = false, length = 40)
  private String metodo;

  @Column(nullable = false)
  private int total;

  @Column(nullable = false)
  private Instant creado;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PedidoItem> items = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(long usuarioId) {
    this.usuarioId = usuarioId;
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

  public List<PedidoItem> getItems() {
    return items;
  }
}
