package cl.sonidovivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.sonidovivo.modelo.Mensaje;
import cl.sonidovivo.repository.MensajeRepositorio;
import jakarta.persistence.EntityManager;
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
class ContactoIntegracionTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private MensajeRepositorio mensajes;

  @Autowired
  private EntityManager em;

  @Test
  void enviarGuardaElMensajeEnLaBase() throws Exception {
    mvc.perform(post("/api/contacto")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"Laura\",\"correo\":\"laura@duoc.cl\",\"comentario\":\"Hola, tienen stock?\"}"))
        .andExpect(status().isCreated());

    em.flush();
    em.clear();
    Mensaje guardado = mensajes.findAll().get(0);
    assertEquals("Laura", guardado.getNombre());
    assertEquals("laura@duoc.cl", guardado.getCorreo());
    assertEquals("Hola, tienen stock?", guardado.getComentario());
  }
}
