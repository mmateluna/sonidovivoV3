package cl.sonidovivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.sonidovivo.dto.LineaRemota;
import cl.sonidovivo.dto.PedidoCreado;
import cl.sonidovivo.modelo.Pago;
import cl.sonidovivo.repository.PagoRepositorio;
import cl.sonidovivo.service.ClienteCarrito;
import cl.sonidovivo.service.ClientePedidos;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PagosIntegracionTest {

  private static final String SECRETO = "sonidovivo-desarrollo-cambiar-esta-clave-por-una-larga";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private PagoRepositorio pagos;

  @Autowired
  private EntityManager em;

  @MockBean
  private ClienteCarrito carrito;

  @MockBean
  private ClientePedidos pedidos;

  @Test
  void cobrarGuardaElPagoYVaciaElCarrito() throws Exception {
    when(carrito.listar(anyString())).thenReturn(List.of(new LineaRemota(1L, "Guitarra", 1000, "", 2, 8)));
    when(pedidos.crear(eq(3L), anyList())).thenReturn(new PedidoCreado(10L, 2000));

    mvc.perform(post("/api/pagos")
            .header("Authorization", "Bearer " + token())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"metodo\":\"debito\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.metodo").value("Tarjeta de débito"))
        .andExpect(jsonPath("$.total").value(2000));

    em.flush();
    em.clear();
    Pago guardado = pagos.findAll().get(0);
    assertEquals(3L, guardado.getUsuarioId());
    assertEquals(10L, guardado.getPedidoId());
    assertEquals(2000, guardado.getTotal());
    verify(carrito).vaciar(anyString());
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
