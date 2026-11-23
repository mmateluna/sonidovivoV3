package cl.sonidovivo.controller;

import cl.sonidovivo.dto.LoginSolicitud;
import cl.sonidovivo.dto.RegistroSolicitud;
import cl.sonidovivo.dto.RenovarSolicitud;
import cl.sonidovivo.dto.SesionRespuesta;
import cl.sonidovivo.dto.UsuarioRespuesta;
import cl.sonidovivo.service.AuthServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Sesión", description = "Login, registro, renovación del JWT y cierre de sesión.")
public class AuthControlador {

  private final AuthServicio servicio;

  public AuthControlador(AuthServicio servicio) {
    this.servicio = servicio;
  }

  @PostMapping("/login")
  @Operation(summary = "Inicia sesión y entrega el token de acceso y el de renovación")
  public SesionRespuesta login(@Valid @RequestBody LoginSolicitud datos) {
    return servicio.login(datos.correo(), datos.clave());
  }

  @PostMapping("/registro")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Registra un cliente e inicia su sesión")
  public SesionRespuesta registro(@Valid @RequestBody RegistroSolicitud datos) {
    return servicio.registrar(datos);
  }

  @PostMapping("/renovar")
  @Operation(summary = "Cambia un refresh token vigente por una sesión nueva")
  public SesionRespuesta renovar(@Valid @RequestBody RenovarSolicitud datos) {
    return servicio.renovar(datos.refresh());
  }

  @GetMapping("/yo")
  @Operation(summary = "Devuelve el usuario del token actual")
  public UsuarioRespuesta yo(Authentication autenticacion) {
    return servicio.yo(autenticacion.getName());
  }

  @PostMapping("/salir")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Anula el refresh token de esta cuenta")
  public void salir(Authentication autenticacion) {
    servicio.salir(autenticacion.getName());
  }
}
