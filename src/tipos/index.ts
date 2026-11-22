// Aquí definimos la "forma" de los datos con TypeScript.
// Si un objeto no cumple con estas interfaces, el editor nos avisa ANTES de ejecutar.

export interface Producto {
  id: number;
  codigo: string;
  nombre: string;
  marca: string;
  modelo: string;
  precio: number;
  categoria: string;
  stock: number;
  stockCritico: number;
  imagen: string;
  descripcion: string;
}

/** Lo que guardamos de cada producto dentro del carrito */
export interface ItemCarrito {
  id: number;
  nombre: string;
  precio: number;
  imagen: string;
  cantidad: number;
  stock: number; // tope al cambiar la cantidad en el carrito
}

export interface Novedad {
  id: number;
  titulo: string;
  fecha: string;
  imagen: string;
  resumen: string;
  parrafos: string[];
}

export interface Region {
  nombre: string;
  comunas: string[];
}

export type TipoUsuario = "Administrador" | "Vendedor" | "Cliente";

export interface UsuarioAdmin {
  id?: number;
  run: string;
  nombre: string;
  apellidos?: string; // el formulario los pide; la semilla antigua no los trae
  correo: string;
  tipo: TipoUsuario;
  region: string;
  comuna: string;
  fechaNacimiento?: string;
  direccion?: string;
}

/** Lo que devuelve el login: los dos tokens y el usuario, sin la contraseña. */
export interface Sesion {
  token: string;
  refresh: string;
  usuario: UsuarioAdmin;
}

/** Variantes de color de Bootstrap que usan nuestros botones */
export type VarianteBoton = "primary" | "secondary" | "danger" | "success";
