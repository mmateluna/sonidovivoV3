/* ============================================
   SONIDO VIVO - repositorio.ts
   Este módulo ya no es la fuente de la tienda.
   El catálogo y el admin piden a la API (servicios/api.ts).
   El patrón Repositorio de la evaluación está en el backend:
   ProductoRepositorio y UsuarioRepositorio.

   Se conserva para las pruebas unitarias. El doble apiMock.ts
   delega aquí, así Vitest no necesita Spring Boot encendido.
   El carrito sigue en su propio Context y en localStorage.
   ============================================ */

import type { Producto, UsuarioAdmin } from "../tipos";
import { productos } from "./productos";
import { usuariosEjemplo } from "./usuarios";

// Claves distintas a la del carrito, para no mezclar los datos.
export const CLAVE_PRODUCTOS = "productosSonidoVivo";
export const CLAVE_USUARIOS = "usuariosSonidoVivo";

/** Copia cada elemento para no modificar el arreglo original de la semilla. */
function copiar<T extends object>(lista: T[]): T[] {
  return lista.map((item) => ({ ...item }));
}

/**
 * Lee una lista guardada.
 * Si la clave no existe, devolvemos la semilla.
 * Si existe "[]", la lista está vacía a propósito (se eliminó todo) y no volvemos a la semilla.
 * Si el JSON está dañado, también volvemos a la semilla.
 */
function leerLista<T extends object>(clave: string, semilla: T[]): T[] {
  try {
    const datos = localStorage.getItem(clave);
    if (datos === null) return copiar(semilla);

    const lista = JSON.parse(datos) as T[];
    if (!Array.isArray(lista)) return copiar(semilla);
    return lista;
  } catch {
    return copiar(semilla);
  }
}

function guardarLista<T>(clave: string, lista: T[]): void {
  localStorage.setItem(clave, JSON.stringify(lista));
}

/* -------------------- Productos -------------------- */

export function listarProductos(): Producto[] {
  return leerLista(CLAVE_PRODUCTOS, productos);
}

export function obtenerProducto(id: number): Producto | undefined {
  return listarProductos().find((producto) => producto.id === id);
}

/** Crea un producto nuevo. El id lo pone el repositorio (el más alto + 1). */
export function crearProducto(datos: Omit<Producto, "id">): Producto {
  const lista = listarProductos();
  const nuevoId = lista.reduce((maximo, producto) => Math.max(maximo, producto.id), 0) + 1;
  const nuevo: Producto = { ...datos, id: nuevoId };

  lista.push(nuevo);
  guardarLista(CLAVE_PRODUCTOS, lista);
  return nuevo;
}

/** Cambia los campos indicados. El id no se puede cambiar. undefined si no existe. */
export function actualizarProducto(
  id: number,
  cambios: Partial<Omit<Producto, "id">>,
): Producto | undefined {
  const lista = listarProductos();
  const indice = lista.findIndex((producto) => producto.id === id);
  if (indice === -1) return undefined;

  const actualizado: Producto = { ...lista[indice], ...cambios, id };
  lista[indice] = actualizado;
  guardarLista(CLAVE_PRODUCTOS, lista);
  return actualizado;
}

export function eliminarProducto(id: number): void {
  const lista = listarProductos().filter((producto) => producto.id !== id);
  guardarLista(CLAVE_PRODUCTOS, lista);
}

/* -------------------- Usuarios -------------------- */

export function listarUsuarios(): UsuarioAdmin[] {
  return leerLista(CLAVE_USUARIOS, usuariosEjemplo);
}

export function obtenerUsuario(run: string): UsuarioAdmin | undefined {
  return listarUsuarios().find((usuario) => usuario.run === run);
}

/** Crea un usuario. Si el RUN ya existe, no agregamos otro y devolvemos undefined. */
export function crearUsuario(datos: UsuarioAdmin): UsuarioAdmin | undefined {
  const lista = listarUsuarios();
  if (lista.some((usuario) => usuario.run === datos.run)) return undefined;

  const nuevo = { ...datos };
  lista.push(nuevo);
  guardarLista(CLAVE_USUARIOS, lista);
  return nuevo;
}

/** Cambia los campos indicados. El RUN no se puede cambiar. undefined si no existe. */
export function actualizarUsuario(
  run: string,
  cambios: Partial<Omit<UsuarioAdmin, "run">>,
): UsuarioAdmin | undefined {
  const lista = listarUsuarios();
  const indice = lista.findIndex((usuario) => usuario.run === run);
  if (indice === -1) return undefined;

  const actualizado: UsuarioAdmin = { ...lista[indice], ...cambios, run };
  lista[indice] = actualizado;
  guardarLista(CLAVE_USUARIOS, lista);
  return actualizado;
}

export function eliminarUsuario(run: string): void {
  const lista = listarUsuarios().filter((usuario) => usuario.run !== run);
  guardarLista(CLAVE_USUARIOS, lista);
}
