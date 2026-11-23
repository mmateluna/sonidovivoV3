package cl.sonidovivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.sonidovivo.modelo.Producto;
import cl.sonidovivo.repository.ProductoRepositorio;
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
class CatalogoIntegracionTest {

  private static final String SECRETO = "sonidovivo-desarrollo-cambiar-esta-clave-por-una-larga";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ProductoRepositorio productos;

  @Autowired
  private EntityManager em;

  @Test
  void elCatalogoEsPublicoYSoloStaffCrea() throws Exception {
    mvc.perform(get("/api/productos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].codigo").exists());

    mvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(producto("ZZ999", "Producto de prueba", 1000, 3)))
        .andExpect(status().isUnauthorized());

    mvc.perform(post("/api/productos")
            .header("Authorization", "Bearer " + token("CLIENTE"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(producto("ZZ998", "Producto de prueba", 1000, 3)))
        .andExpect(status().isForbidden());

    mvc.perform(post("/api/productos")
            .header("Authorization", "Bearer " + token("VENDEDOR"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(producto("ZZ998", "Producto de prueba", 1000, 3)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nombre").value("Producto de prueba"));
  }

  @Test
  void elAdminCreaYEditaYLaFilaQuedaEnLaBase() throws Exception {
    String admin = token("ADMINISTRADOR");

    String creado = mvc.perform(post("/api/productos")
            .header("Authorization", "Bearer " + admin)
            .contentType(MediaType.APPLICATION_JSON)
            .content(producto("ZZ100", "Producto de prueba", 1000, 3)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();
    long id = ((Number) JsonPath.read(creado, "$.id")).longValue();

    // Vacía la caché de JPA para que la siguiente lectura salga de la base, no de la memoria.
    em.flush();
    em.clear();

    Producto guardado = productos.findByCodigo("ZZ100").orElseThrow();
    assertEquals(id, guardado.getId());
    assertEquals("Producto de prueba", guardado.getNombre());
    assertEquals(1000, guardado.getPrecio());
    assertEquals(3, guardado.getStock());

    mvc.perform(put("/api/productos/" + id)
            .header("Authorization", "Bearer " + admin)
            .contentType(MediaType.APPLICATION_JSON)
            .content(producto("ZZ100", "Producto editado", 2500, 9)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Producto editado"));

    em.flush();
    em.clear();

    Producto editado = productos.findByCodigo("ZZ100").orElseThrow();
    assertEquals("Producto editado", editado.getNombre());
    assertEquals(2500, editado.getPrecio());
    assertEquals(9, editado.getStock());
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

  private String producto(String codigo, String nombre, int precio, int stock) {
    return """
        {"codigo":"%s","nombre":"%s","marca":"Sonido","modelo":"Vivo","precio":%d,"categoria":"Baterías","stock":%d,"stockCritico":1,"imagen":"","descripcion":"Prueba de base"}
        """.formatted(codigo, nombre, precio, stock);
  }
}
