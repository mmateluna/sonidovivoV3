package cl.sonidovivo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuariosIntegracionTest {

  private static final String SECRETO = "sonidovivo-desarrollo-cambiar-esta-clave-por-una-larga";

  @Autowired
  private MockMvc mvc;

  @Test
  void elClienteNoListaUsuariosYElAdminSi() throws Exception {
    mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token("CLIENTE")))
        .andExpect(status().isForbidden());

    mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token("ADMINISTRADOR")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].correo").exists());
  }

  @Test
  void validarLeeLaClaveGuardadaEnLaBase() throws Exception {
    mvc.perform(post("/api/interno/validar")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"juan@gmail.com\",\"clave\":\"juan1234\"}"))
        .andExpect(status().isForbidden());

    mvc.perform(post("/api/interno/validar")
            .header("X-Interno", "sonidovivo-interno")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"juan@gmail.com\",\"clave\":\"juan1234\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
        .andExpect(jsonPath("$.correo").value("juan@gmail.com"));
  }

  private String token(String rol) {
    SecretKey clave = Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .subject("prueba@duoc.cl")
        .claim("rol", rol)
        .claim("nombre", "Prueba")
        .claim("tipo", "acceso")
        .claim("uid", 1)
        .issuedAt(new Date())
        .expiration(Date.from(Instant.now().plusSeconds(3600)))
        .signWith(clave)
        .compact();
  }
}
