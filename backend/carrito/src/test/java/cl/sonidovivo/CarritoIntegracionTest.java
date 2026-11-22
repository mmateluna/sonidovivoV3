package cl.sonidovivo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class CarritoIntegracionTest {

  private static final String SECRETO = "sonidovivo-desarrollo-cambiar-esta-clave-por-una-larga";

  @Autowired
  private MockMvc mvc;

  @Test
  void guardarYPagarDejaElPedidoYVaciaElCarrito() throws Exception {
    mvc.perform(get("/api/carrito")).andExpect(status().isUnauthorized());

    String token = token();
    String linea = """
        {"id":1,"nombre":"Guitarra","precio":1000,"imagen":"","cantidad":2,"stock":8}
        """;

    mvc.perform(put("/api/carrito")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(linea))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cantidad").value(2));

    mvc.perform(post("/api/pedidos")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"metodo\":\"debito\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.total").value(2000))
        .andExpect(jsonPath("$.metodo").value("Tarjeta de débito"));

    mvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  private String token() {
    SecretKey clave = Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .subject("pedro@gmail.com")
        .claim("rol", "CLIENTE")
        .claim("nombre", "Pedro")
        .claim("tipo", "acceso")
        .claim("uid", 3)
        .issuedAt(new Date())
        .expiration(Date.from(Instant.now().plusSeconds(3600)))
        .signWith(clave)
        .compact();
  }
}
