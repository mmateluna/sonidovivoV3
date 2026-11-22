package cl.sonidovivo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

// El refresh token se guarda para poder anularlo al cerrar sesión.
@Entity
public class TokenRefresco {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 700)
  private String valor;

  @ManyToOne(optional = false)
  private Usuario usuario;

  @Column(nullable = false)
  private Instant expira;

  public Long getId() {
    return id;
  }

  public String getValor() {
    return valor;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Instant getExpira() {
    return expira;
  }

  public void setExpira(Instant expira) {
    this.expira = expira;
  }
}
