import { useEffect, useState, type ChangeEvent } from "react";
import ListaProductos from "../componentes/organismos/ListaProductos";
import FiltroCatalogo from "../componentes/moleculas/FiltroCatalogo";
import { listarProductos } from "../servicios/api";
import type { Producto } from "../tipos";

function Catalogo() {
  const [nombre, setNombre] = useState("");
  const [categoria, setCategoria] = useState("");
  const [productos, setProductos] = useState<Producto[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    listarProductos()
      .then(setProductos)
      .catch(() => setError("No se pudo cargar el catálogo. Revisa que el backend esté en marcha."))
      .finally(() => setCargando(false));
  }, []);

  const texto = nombre.trim().toLowerCase();
  // Las categorías del filtro salen de los productos que hay ahora, no de una lista fija.
  const categorias = Array.from(new Set(productos.map((producto) => producto.categoria))).sort();
  const visibles = productos.filter((producto) => {
    const coincideNombre = producto.nombre.toLowerCase().includes(texto);
    const coincideCategoria = categoria === "" || producto.categoria === categoria;
    return coincideNombre && coincideCategoria;
  });

  const cambiarNombre = (evento: ChangeEvent<HTMLInputElement>) => {
    setNombre(evento.target.value);
  };

  const cambiarCategoria = (evento: ChangeEvent<HTMLSelectElement>) => {
    setCategoria(evento.target.value);
  };

  return (
    <div className="container py-4">
      <h2>Catálogo de productos</h2>
      <p className="mb-4">Explora nuestras guitarras, baterías, teclados y más.</p>
      <FiltroCatalogo
        nombre={nombre}
        categoria={categoria}
        categorias={categorias}
        onNombre={cambiarNombre}
        onCategoria={cambiarCategoria}
      />
      {cargando ? (
        <p>Cargando catálogo...</p>
      ) : error ? (
        <p>{error}</p>
      ) : visibles.length === 0 ? (
        <p>No hay productos que coincidan con la búsqueda.</p>
      ) : (
        <ListaProductos productos={visibles} />
      )}
    </div>
  );
}

export default Catalogo;
