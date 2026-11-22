package cl.sonidovivo.web;

import cl.sonidovivo.dto.UsuarioRespuesta;
import cl.sonidovivo.dto.UsuarioSolicitud;
import cl.sonidovivo.negocio.UsuarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD de cuentas. Solo el rol Administrador puede usar estos endpoints.")
public class UsuarioControlador {

  private final UsuarioServicio servicio;

  public UsuarioControlador(UsuarioServicio servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  @Operation(summary = "Lista los usuarios")
  public List<UsuarioRespuesta> listar() {
    return servicio.listar();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crea un usuario con el rol indicado")
  public UsuarioRespuesta crear(@Valid @RequestBody UsuarioSolicitud datos) {
    return servicio.crear(datos);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualiza un usuario. La clave es opcional.")
  public UsuarioRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioSolicitud datos) {
    return servicio.actualizar(id, datos);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Elimina un usuario")
  public void eliminar(@PathVariable Long id, Authentication autenticacion) {
    servicio.eliminar(id, autenticacion.getName());
  }
}
