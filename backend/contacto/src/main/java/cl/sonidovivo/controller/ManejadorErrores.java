package cl.sonidovivo.controller;

import cl.sonidovivo.service.ReglaNegocioException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ManejadorErrores {

  @ExceptionHandler(ReglaNegocioException.class)
  public ResponseEntity<Map<String, String>> regla(ReglaNegocioException ex) {
    return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
    String mensaje = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(error -> error.getDefaultMessage() == null ? "Los datos no son válidos." : error.getDefaultMessage())
        .orElse("Los datos no son válidos.");
    return ResponseEntity.badRequest().body(Map.of("mensaje", mensaje));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> estado(ResponseStatusException ex) {
    String mensaje = ex.getReason() == null ? "No se pudo completar la operación." : ex.getReason();
    return ResponseEntity.status(ex.getStatusCode()).body(Map.of("mensaje", mensaje));
  }
}
