package cl.sonidovivo.controller;

import cl.sonidovivo.dto.LoginSolicitud;
import cl.sonidovivo.dto.PerfilInterno;
import cl.sonidovivo.dto.RegistroSolicitud;
import cl.sonidovivo.service.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

// Estas rutas no las llama el navegador. Las llama el servicio de auth.
@RestController
@RequestMapping("/api/interno")
public class InternoControlador {

  private final UsuarioServicio servicio;
  private final String secreto;

  public InternoControlador(UsuarioServicio servicio, @Value("${sonidovivo.interno.secreto}") String secreto) {
    this.servicio = servicio;
    this.secreto = secreto;
  }

  @PostMapping("/validar")
  public PerfilInterno validar(@Valid @RequestBody LoginSolicitud datos, HttpServletRequest request) {
    exigir(request);
    return servicio.validar(datos.correo(), datos.clave());
  }

  @PostMapping("/registro")
  public PerfilInterno registrar(@Valid @RequestBody RegistroSolicitud datos, HttpServletRequest request) {
    exigir(request);
    return servicio.registrar(datos);
  }

  @GetMapping("/usuarios/{id}")
  public PerfilInterno porId(@PathVariable Long id, HttpServletRequest request) {
    exigir(request);
    return servicio.porId(id);
  }

  @GetMapping("/correo")
  public PerfilInterno porCorreo(@RequestParam String correo, HttpServletRequest request) {
    exigir(request);
    return servicio.porCorreo(correo);
  }

  private void exigir(HttpServletRequest request) {
    if (!secreto.equals(request.getHeader("X-Interno"))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción.");
    }
  }
}
