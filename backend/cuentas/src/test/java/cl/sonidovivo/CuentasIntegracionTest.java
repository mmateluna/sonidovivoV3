package cl.sonidovivo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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
class CuentasIntegracionTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void elClienteNoListaUsuariosYElAdminSi() throws Exception {
    String cliente = login("pedro@gmail.com", "pedro1234");
    mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token(cliente)))
        .andExpect(status().isForbidden());

    String admin = login("juan@gmail.com", "juan1234");
    mvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token(admin)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].correo").exists());
  }

  @Test
  void renovarEntregaOtroToken() throws Exception {
    String sesion = login("juan@gmail.com", "juan1234");
    String refresh = JsonPath.read(sesion, "$.refresh");

    mvc.perform(post("/api/auth/renovar")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refresh\":\"" + refresh + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.usuario.tipo").value("Administrador"));
  }

  private String login(String correo, String clave) throws Exception {
    return mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"" + correo + "\",\"clave\":\"" + clave + "\"}"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  private String token(String cuerpo) {
    return JsonPath.read(cuerpo, "$.token");
  }
}
