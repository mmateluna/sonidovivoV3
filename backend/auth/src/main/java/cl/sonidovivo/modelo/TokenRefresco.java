package cl.sonidovivo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

// El refresh token se guarda aquí para poder anularlo. El usuario vive en otro servicio.
@Entity
public class TokenRefresco {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 700)
  private String valor;

  @Column(name = "usuario_id", nullable = false)
  private long usuarioId;

  @Column(nullable = false, length = 100)
  private String correo;

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

  public long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(long usuarioId) {
    this.usuarioId = usuarioId;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public Instant getExpira() {
    return expira;
  }

  public void setExpira(Instant expira) {
    this.expira = expira;
  }
}
