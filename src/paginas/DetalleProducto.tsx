import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import BotonLink from "../componentes/atomos/BotonLink";
import BotonAgregar from "../componentes/moleculas/BotonAgregar";
import SelectorCantidad from "../componentes/moleculas/SelectorCantidad";
import { useCarrito } from "../contexto/CarritoContext";
import { obtenerProducto } from "../servicios/api";
import { formatearPrecio } from "../utilidades/formato";
import type { Producto } from "../tipos";

function DetalleProducto() {
  const { id } = useParams();
  const { agregar } = useCarrito();
  const [producto, setProducto] = useState<Producto | null | undefined>(undefined);
  const [cantidad, setCantidad] = useState(1);

  useEffect(() => {
    obtenerProducto(Number(id))
      .then(setProducto)
      .catch(() => setProducto(null));
  }, [id]);

  if (producto === undefined) {
    return (
      <div className="container py-4">
        <p>Cargando producto...</p>
      </div>
    );
  }

  if (!producto) {
    return (
      <div className="container py-4">
        <p>Producto no encontrado.</p>
        <BotonLink to="/catalogo" variante="secondary">
          Volver al catálogo
        </BotonLink>
      </div>
    );
  }

  const hayStock = producto.stock > 0;

  return (
    <div className="container py-4">
      <div className="row g-4">
        <div className="col-12 col-md-6">
          <div className="bg-white rounded p-3 border">
            <img src={producto.imagen} alt={producto.nombre} className="img-fluid d-block mx-auto" style={{ maxHeight: "420px", objectFit: "contain" }} />
          </div>
        </div>

        <div className="col-12 col-md-6">
          <p className="categoria small text-uppercase mb-1">{producto.categoria}</p>
          <h2>{producto.nombre}</h2>
          {(producto.marca || producto.modelo) && (
            <p>
              <strong>{producto.marca}</strong>
              {producto.modelo ? ` · ${producto.modelo}` : ""}
            </p>
          )}
          <p className="precio display-6 fw-bold my-3">{formatearPrecio(producto.precio)}</p>
          <p>{producto.descripcion}</p>
          <p>{hayStock ? `Stock disponible: ${producto.stock}` : "Sin stock"}</p>

          <div className="d-flex flex-wrap align-items-end gap-2 mt-4">
            {hayStock && (
              <SelectorCantidad
                id="cantidad-detalle"
                valor={cantidad}
                maximo={producto.stock}
                onChange={setCantidad}
              />
            )}
            <BotonAgregar
              texto="Agregar al carrito"
              deshabilitado={!hayStock}
              onAgregar={() => agregar(producto, cantidad)}
            />
            <BotonLink to="/catalogo" variante="secondary">
              Volver al catálogo
            </BotonLink>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DetalleProducto;
