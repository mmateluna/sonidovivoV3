package cl.sonidovivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.sonidovivo.modelo.Pedido;
import cl.sonidovivo.repository.PedidoRepositorio;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
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
class PedidosIntegracionTest {

  private static final String SECRETO = "sonidovivo-desarrollo-cambiar-esta-clave-por-una-larga";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private PedidoRepositorio pedidos;

  @Autowired
  private EntityManager em;

  @Test
  void crearPorLaViaInternaDejaLaFilaEnLaBase() throws Exception {
    String cuerpo = """
        {"usuarioId":3,"lineas":[{"productoId":1,"nombre":"Guitarra","precio":1000,"cantidad":2}]}
        """;

    String creado = mvc.perform(post("/api/interno/pedidos")
            .header("X-Interno", "sonidovivo-interno")
            .contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.total").value(2000))
        .andReturn()
        .getResponse()
        .getContentAsString();

    long id = ((Number) JsonPath.read(creado, "$.id")).longValue();
    em.flush();
    em.clear();
    Pedido guardado = pedidos.findById(id).orElseThrow();
    assertEquals(3L, guardado.getUsuarioId());
    assertEquals(2000, guardado.getTotal());
    assertEquals(1, guardado.getItems().size());

    mvc.perform(get("/api/pedidos").header("Authorization", "Bearer " + token()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id))
        .andExpect(jsonPath("$[0].total").value(2000));
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
