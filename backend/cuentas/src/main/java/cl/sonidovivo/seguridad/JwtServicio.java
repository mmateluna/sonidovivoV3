package cl.sonidovivo.seguridad;

import cl.sonidovivo.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServicio {

  private final SecretKey clave;
  private final long minutosAcceso;
  private final long diasRefresh;

  public JwtServicio(
      @Value("${sonidovivo.jwt.secreto}") String secreto,
      @Value("${sonidovivo.jwt.minutos-acceso}") long minutosAcceso,
      @Value("${sonidovivo.jwt.dias-refresh}") long diasRefresh
  ) {
    this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    this.minutosAcceso = minutosAcceso;
    this.diasRefresh = diasRefresh;
  }

  public String crearAcceso(Usuario usuario) {
    return firmar(usuario, "acceso", Instant.now().plusSeconds(minutosAcceso * 60));
  }

  public String crearRefresh(Usuario usuario) {
    return firmar(usuario, "refresh", Instant.now().plusSeconds(diasRefresh * 24 * 60 * 60));
  }

  public Instant vencimientoRefresh() {
    return Instant.now().plusSeconds(diasRefresh * 24 * 60 * 60);
  }

  // Devuelve los datos del token o lanza si la firma no calza o ya venció.
  public Claims leer(String token) {
    return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
  }

  private String firmar(Usuario usuario, String tipo, Instant vence) {
    return Jwts.builder()
        // Cada emisión lleva un id propio. Si no, dos tokens del mismo segundo serían iguales.
        .id(UUID.randomUUID().toString())
        .subject(usuario.getCorreo())
        // uid lo leen catálogo y carrito: no necesitan consultar esta base.
        .claim("uid", usuario.getId())
        .claim("rol", usuario.getRol().name())
        .claim("nombre", usuario.getNombre())
        .claim("tipo", tipo)
        .issuedAt(new Date())
        .expiration(Date.from(vence))
        .signWith(clave)
        .compact();
  }
}
