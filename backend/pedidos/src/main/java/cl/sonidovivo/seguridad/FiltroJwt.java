package cl.sonidovivo.seguridad;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Además del rol, deja el id del usuario en los detalles. El carrito no tiene tabla de usuarios.
@Component
public class FiltroJwt extends OncePerRequestFilter {

  private final JwtServicio jwt;

  public FiltroJwt(JwtServicio jwt) {
    this.jwt = jwt;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      try {
        Claims claims = jwt.leer(header.substring(7));
        if ("acceso".equals(claims.get("tipo", String.class))) {
          String rol = claims.get("rol", String.class);
          var autenticacion = new UsernamePasswordAuthenticationToken(
              claims.getSubject(),
              null,
              List.of(new SimpleGrantedAuthority("ROLE_" + rol))
          );
          Object uid = claims.get("uid");
          if (uid instanceof Number numero) {
            autenticacion.setDetails(numero.longValue());
          }
          SecurityContextHolder.getContext().setAuthentication(autenticacion);
        }
      } catch (RuntimeException ex) {
        // Token vencido o alterado: la petición sigue sin usuario.
      }
    }
    chain.doFilter(request, response);
  }
}
