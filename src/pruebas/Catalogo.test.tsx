import { describe, expect, test, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { CarritoProvider } from "../contexto/CarritoContext";
import { AuthProvider } from "../contexto/AuthContext";
import Catalogo from "../paginas/Catalogo";
import { productos } from "../datos/productos";

vi.mock("../servicios/api", async () => {
  return await import("./apiMock");
});

// El catálogo usa enlaces y el botón de agregar, que pide el carrito.
function renderizarCatalogo() {
  return render(
    <MemoryRouter>
      <AuthProvider>
        <CarritoProvider>
          <Catalogo />
        </CarritoProvider>
      </AuthProvider>
    </MemoryRouter>,
  );
}

describe("filtro del catálogo", () => {
  test("un texto deja solo algunos productos y vaciarlo los muestra todos", async () => {
    const usuario = userEvent.setup();
    renderizarCatalogo();
    await screen.findByRole("heading", { name: "Guitarra Acústica Folk" });

    const coincidencias = productos.filter((producto) => producto.nombre.toLowerCase().includes("guitarra"));
    await usuario.type(screen.getByLabelText("Nombre"), "guitarra");

    expect(screen.getAllByRole("button", { name: "Agregar" })).toHaveLength(coincidencias.length);
    expect(coincidencias.length).toBeGreaterThan(1);
    expect(coincidencias.length).toBeLessThan(productos.length);
    expect(screen.queryByRole("heading", { name: "Batería Acústica 5 piezas" })).not.toBeInTheDocument();

    await usuario.clear(screen.getByLabelText("Nombre"));

    expect(screen.getAllByRole("button", { name: "Agregar" })).toHaveLength(productos.length);
  });
});
