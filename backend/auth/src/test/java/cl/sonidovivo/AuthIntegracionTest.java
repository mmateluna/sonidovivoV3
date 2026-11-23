package cl.sonidovivo;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.sonidovivo.dto.PerfilInterno;
import cl.sonidovivo.modelo.TokenRefresco;
import cl.sonidovivo.repository.TokenRefrescoRepositorio;
import cl.sonidovivo.service.ClienteUsuarios;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
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
class AuthIntegracionTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private TokenRefrescoRepositorio tokens;

  @Autowired
  private EntityManager em;

  @MockBean
  private ClienteUsuarios usuarios;

  @Test
  void loginGuardaElRefreshYRenovarLoRota() throws Exception {
    PerfilInterno juan = new PerfilInterno(
        1L, "123456785", "Juan", "González", "juan@gmail.com",
        "Administrador", "ADMINISTRADOR", "Región de Valparaíso", "Viña del Mar", "Calle 1", ""
    );
    when(usuarios.validar("juan@gmail.com", "juan1234")).thenReturn(juan);
    when(usuarios.porId(1L)).thenReturn(juan);

    String sesion = mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"juan@gmail.com\",\"clave\":\"juan1234\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.usuario.tipo").value("Administrador"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    em.flush();
    em.clear();
    String refresh = JsonPath.read(sesion, "$.refresh");
    TokenRefresco guardado = tokens.findByValor(refresh).orElseThrow();
    org.junit.jupiter.api.Assertions.assertEquals(1L, guardado.getUsuarioId());

    mvc.perform(post("/api/auth/renovar")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refresh\":\"" + refresh + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.usuario.tipo").value("Administrador"));

    em.flush();
    em.clear();
    org.junit.jupiter.api.Assertions.assertTrue(tokens.findByValor(refresh).isEmpty());
  }
}
