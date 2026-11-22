import { useState } from "react";
import BotonLink from "../atomos/BotonLink";
import BotonAgregar from "./BotonAgregar";
import SelectorCantidad from "./SelectorCantidad";
import { formatearPrecio } from "../../utilidades/formato";

interface CardProductoProps {
  id: number;
  titulo: string;
  categoria: string;
  precio: number;
  imagen: string;
  stock: number;
  onAgregar: (id: number, cantidad: number) => void;
}

function CardProducto({ id, titulo, categoria, precio, imagen, stock, onAgregar }: CardProductoProps) {
  const [cantidad, setCantidad] = useState(1);
  const hayStock = stock > 0;

  return (
    <div className="card tarjeta-producto h-100">
      <div className="bg-white p-2">
        <img src={imagen} alt={titulo} className="card-img-top" style={{ height: "180px", objectFit: "contain" }} />
      </div>

      <div className="card-body d-flex flex-column">
        <p className="categoria small text-uppercase mb-1">{categoria}</p>
        <h3 className="card-title h6">{titulo}</h3>
        <p className="precio fw-bold fs-5 mb-1">{formatearPrecio(precio)}</p>
        <p className="small mb-2">{hayStock ? `Stock: ${stock}` : "Sin stock"}</p>

        <div className="mt-auto d-flex flex-wrap align-items-end gap-2">
          {hayStock && (
            <SelectorCantidad id={`cantidad-${id}`} valor={cantidad} maximo={stock} onChange={setCantidad} />
          )}
          <BotonLink to={`/producto/${id}`} variante="secondary" pequeno>
            Ver detalle
          </BotonLink>
          <BotonAgregar pequeno deshabilitado={!hayStock} onAgregar={() => onAgregar(id, cantidad)} />
        </div>
      </div>
    </div>
  );
}

export default CardProducto;
