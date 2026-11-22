/* Doble del backend para Vitest.
   Las pantallas importan servicios/api.ts; las pruebas reemplazan ese archivo por este.
   Así el catálogo y el admin se prueban sin tener Spring Boot encendido.
   Los productos siguen saliendo del repositorio local, que lee la semilla. */

import type { ItemCarrito, Producto, Sesion, UsuarioAdmin } from "../tipos";
import type { DatosUsuario } from "../utilidades/validaciones";
import {
  actualizarProducto as actualizarLocal,
  crearProducto as crearLocal,
  eliminarProducto as eliminarLocal,
  listarProductos as listarLocal,
  obtenerProducto as obtenerLocal,
} from "../datos/repositorio";

const sesionDePrueba: Sesion = {
  token: "token-prueba",
  refresh: "refresh-prueba",
  usuario: {
    id: 1,
    run: "123456785",
    nombre: "Laura",
    apellidos: "Díaz",
    correo: "laura@duoc.cl",
    tipo: "Cliente",
    region: "Región de Valparaíso",
    comuna: "Viña del Mar",
    direccion: "Calle 1",
  },
};

export async function listarProductos(): Promise<Producto[]> {
  return listarLocal();
}

export async function obtenerProducto(id: number): Promise<Producto | null> {
  return obtenerLocal(id) ?? null;
}

export async function crearProducto(datos: Omit<Producto, "id">): Promise<Producto> {
  return crearLocal(datos);
}

export async function actualizarProducto(id: number, datos: Omit<Producto, "id">): Promise<Producto> {
  const actualizado = actualizarLocal(id, datos);
  if (!actualizado) throw new Error("Producto no encontrado.");
  return actualizado;
}

export async function eliminarProducto(id: number): Promise<void> {
  eliminarLocal(id);
}

export async function listarUsuarios(): Promise<UsuarioAdmin[]> {
  return [];
}

export async function crearUsuario(_datos: DatosUsuario): Promise<UsuarioAdmin> {
  return sesionDePrueba.usuario;
}

export async function actualizarUsuario(_id: number, _datos: DatosUsuario): Promise<UsuarioAdmin> {
  return sesionDePrueba.usuario;
}

export async function eliminarUsuario(_id: number): Promise<void> {}

export async function login(_correo: string, _clave: string): Promise<Sesion> {
  return sesionDePrueba;
}

export async function registrar(_datos: DatosUsuario): Promise<Sesion> {
  return sesionDePrueba;
}

export async function yo(): Promise<UsuarioAdmin> {
  return sesionDePrueba.usuario;
}

export async function salir(): Promise<void> {}

export async function listarCarrito(): Promise<ItemCarrito[]> {
  return [];
}

export async function guardarItemCarrito(item: ItemCarrito): Promise<ItemCarrito> {
  return item;
}

export async function eliminarItemCarrito(_id: number): Promise<void> {}

export async function vaciarCarrito(): Promise<void> {}

export async function crearPedido(metodo: string) {
  return { id: 1, metodo, total: 0 };
}

export function usarCredenciales(_token: string | null, _refresh: string | null) {}

export function credencialesActuales() {
  return { token: "token-prueba", refresh: "refresh-prueba" };
}

export function alRenovar(_fn: (token: string, refresh: string) => void) {}

export function alPerderSesion(_fn: () => void) {}
