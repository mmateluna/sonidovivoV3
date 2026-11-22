package cl.sonidovivo.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Este servicio no emite tokens. Solo comprueba los que firmó cuentas, con la misma clave.
@Service
public class JwtServicio {

  private final SecretKey clave;

  public JwtServicio(@Value("${sonidovivo.jwt.secreto}") String secreto) {
    this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
  }

  public Claims leer(String token) {
    return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
  }
}
