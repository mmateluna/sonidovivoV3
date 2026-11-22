import Boton from "../atomos/Boton";
import SelectorCantidad from "./SelectorCantidad";
import { formatearPrecio } from "../../utilidades/formato";

interface FilaCarritoProps {
  id: number;
  nombre: string;
  imagen: string;
  precio: number;
  cantidad: number;
  stock: number;
  onEliminar: (id: number) => void;
  onCambiarCantidad: (id: number, cantidad: number) => void;
}

function FilaCarrito({ id, nombre, imagen, precio, cantidad, stock, onEliminar, onCambiarCantidad }: FilaCarritoProps) {
  const tope = stock > 0 ? stock : 99;

  return (
    <tr>
      <td>
        <img src={imagen} alt={nombre} style={{ width: "64px", height: "64px", objectFit: "contain", background: "#fff" }} />
      </td>
      <td>{nombre}</td>
      <td>{formatearPrecio(precio)}</td>
      <td>
        <SelectorCantidad
          id={`carrito-cantidad-${id}`}
          valor={cantidad}
          maximo={tope}
          onChange={(nueva) => onCambiarCantidad(id, nueva)}
        />
      </td>
      <td>{formatearPrecio(precio * cantidad)}</td>
      <td>
        <Boton variante="danger" pequeno onClick={() => onEliminar(id)}>
          Eliminar
        </Boton>
      </td>
    </tr>
  );
}

export default FilaCarrito;
