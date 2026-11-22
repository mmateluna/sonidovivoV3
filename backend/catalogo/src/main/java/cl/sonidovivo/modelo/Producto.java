package cl.sonidovivo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Un instrumento o equipo del catálogo. Los nombres coinciden con el Producto de TypeScript.
@Entity
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 20)
  private String codigo;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(length = 80)
  private String marca;

  @Column(length = 80)
  private String modelo;

  @Column(nullable = false)
  private int precio;

  @Column(nullable = false, length = 80)
  private String categoria;

  @Column(nullable = false)
  private int stock;

  @Column(nullable = false)
  private int stockCritico;

  @Column(length = 300)
  private String imagen;

  @Column(length = 500)
  private String descripcion;

  public Long getId() {
    return id;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getMarca() {
    return marca;
  }

  public void setMarca(String marca) {
    this.marca = marca;
  }

  public String getModelo() {
    return modelo;
  }

  public void setModelo(String modelo) {
    this.modelo = modelo;
  }

  public int getPrecio() {
    return precio;
  }

  public void setPrecio(int precio) {
    this.precio = precio;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  public int getStockCritico() {
    return stockCritico;
  }

  public void setStockCritico(int stockCritico) {
    this.stockCritico = stockCritico;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
}
