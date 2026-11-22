import { describe, expect, test, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { CarritoProvider } from "../contexto/CarritoContext";
import { AuthProvider } from "../contexto/AuthContext";
import DetalleProducto from "../paginas/DetalleProducto";
import { productos } from "../datos/productos";
import { formatearPrecio } from "../utilidades/formato";

vi.mock("../servicios/api", async () => {
  return await import("./apiMock");
});

// Montamos la ruta dinámica real (/producto/:id) y "visitamos" una URL concreta.
function visitar(url: string) {
  return render(
    <MemoryRouter initialEntries={[url]}>
      <AuthProvider>
        <CarritoProvider>
          <Routes>
            <Route path="/producto/:id" element={<DetalleProducto />} />
          </Routes>
        </CarritoProvider>
      </AuthProvider>
    </MemoryRouter>,
  );
}

describe("DetalleProducto", () => {
  test("muestra el producto que indica el id de la URL", async () => {
    const producto = productos[2];
    visitar(`/producto/${producto.id}`);

    expect(await screen.findByRole("heading", { name: producto.nombre })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Volver al catálogo" })).toHaveAttribute("href", "/catalogo");
  });

  test("muestra el precio y el botón de agregar", async () => {
    const producto = productos[0];
    visitar(`/producto/${producto.id}`);

    expect(await screen.findByText(formatearPrecio(producto.precio))).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Agregar al carrito" })).toBeInTheDocument();
  });

  test("si el id no existe muestra un mensaje", async () => {
    visitar("/producto/9999");
    expect(await screen.findByText("Producto no encontrado.")).toBeInTheDocument();
  });
});
