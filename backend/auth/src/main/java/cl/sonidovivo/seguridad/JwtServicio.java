package cl.sonidovivo.seguridad;

import cl.sonidovivo.dto.PerfilInterno;
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

  public String crearAcceso(PerfilInterno perfil) {
    return firmar(perfil, "acceso", Instant.now().plusSeconds(minutosAcceso * 60));
  }

  public String crearRefresh(PerfilInterno perfil) {
    return firmar(perfil, "refresh", Instant.now().plusSeconds(diasRefresh * 24 * 60 * 60));
  }

  public Instant vencimientoRefresh() {
    return Instant.now().plusSeconds(diasRefresh * 24 * 60 * 60);
  }

  public Claims leer(String token) {
    return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
  }

  private String firmar(PerfilInterno perfil, String tipo, Instant vence) {
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(perfil.correo())
        .claim("uid", perfil.id())
        .claim("rol", perfil.rol())
        .claim("nombre", perfil.nombre())
        .claim("tipo", tipo)
        .issuedAt(new Date())
        .expiration(Date.from(vence))
        .signWith(clave)
        .compact();
  }
}
