package cl.sonidovivo.controller;

import cl.sonidovivo.dto.ProductoSolicitud;
import cl.sonidovivo.modelo.Producto;
import cl.sonidovivo.service.ProductoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD del catálogo. Leer es público; crear, editar y eliminar piden rol de administrador o vendedor.")
public class ProductoControlador {

  private final ProductoServicio servicio;

  public ProductoControlador(ProductoServicio servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  @Operation(summary = "Lista todos los productos")
  public List<Producto> listar() {
    return servicio.listar();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtiene un producto por id")
  public Producto obtener(@PathVariable Long id) {
    return servicio.obtener(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crea un producto")
  public Producto crear(@Valid @RequestBody ProductoSolicitud datos) {
    return servicio.crear(datos);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualiza un producto")
  public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoSolicitud datos) {
    return servicio.actualizar(id, datos);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Elimina un producto")
  public void eliminar(@PathVariable Long id) {
    servicio.eliminar(id);
  }
}
