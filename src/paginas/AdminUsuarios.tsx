import { useEffect, useRef, useState } from "react";
import TablaUsuariosAdmin from "../componentes/organismos/TablaUsuariosAdmin";
import FormularioUsuario from "../componentes/organismos/FormularioUsuario";
import { actualizarUsuario, crearUsuario, eliminarUsuario, listarUsuarios } from "../servicios/api";
import type { DatosUsuario } from "../utilidades/validaciones";
import type { TipoUsuario, UsuarioAdmin } from "../tipos";

function usuarioDesdeFormulario(datos: DatosUsuario): DatosUsuario {
  return {
    ...datos,
    run: datos.run.trim(),
    nombre: datos.nombre.trim(),
    apellidos: datos.apellidos.trim(),
    correo: datos.correo.trim(),
    tipo: datos.tipo as TipoUsuario,
    direccion: datos.direccion.trim(),
  };
}

function AdminUsuarios() {
  const [usuarios, setUsuarios] = useState<UsuarioAdmin[]>([]);
  const [runEditando, setRunEditando] = useState<string | null>(null);
  const [claveFormulario, setClaveFormulario] = useState<string>("nuevo");
  const [versionFormulario, setVersionFormulario] = useState(0);
  const [error, setError] = useState("");
  const seccionFormulario = useRef<HTMLDivElement>(null);

  const usuarioEditando =
    runEditando === null ? null : usuarios.find((usuario) => usuario.run === runEditando) ?? null;

  const recargar = async () => setUsuarios(await listarUsuarios());

  useEffect(() => {
    recargar().catch(() => setError("No se pudieron cargar los usuarios."));
  }, []);

  const editar = (run: string) => {
    setRunEditando(run);
    setClaveFormulario(run);
    seccionFormulario.current?.scrollIntoView?.({ behavior: "smooth" });
  };

  const guardar = async (datos: DatosUsuario): Promise<string | void> => {
    const registro = usuarioDesdeFormulario(datos);
    try {
      if (runEditando === null) {
        const creado = await crearUsuario(registro);
        setRunEditando(creado.run);
      } else {
        const actual = usuarios.find((usuario) => usuario.run === runEditando);
        if (!actual?.id) return "No se encontró el usuario.";
        await actualizarUsuario(actual.id, registro);
      }
      setError("");
      await recargar();
    } catch (fallo) {
      return fallo instanceof Error ? fallo.message : "No se pudo guardar.";
    }
  };

  const eliminar = async (run: string) => {
    const usuario = usuarios.find((item) => item.run === run);
    if (!usuario?.id) return;
    try {
      await eliminarUsuario(usuario.id);
      if (runEditando === run) {
        setRunEditando(null);
        setClaveFormulario("nuevo");
        setVersionFormulario((version) => version + 1);
      }
      setError("");
      await recargar();
    } catch (fallo) {
      setError(fallo instanceof Error ? fallo.message : "No se pudo eliminar.");
    }
  };

  const prepararNuevo = () => {
    setRunEditando(null);
    setClaveFormulario("nuevo");
  };

  const nombreTitulo = usuarioEditando
    ? [usuarioEditando.nombre, usuarioEditando.apellidos].filter(Boolean).join(" ")
    : "";

  return (
    <div className="container py-4">
      <h2 className="mb-3">Gestión de usuarios</h2>
      {error && <p className="text-danger">{error}</p>}
      <TablaUsuariosAdmin usuarios={usuarios} onEditar={editar} onEliminar={eliminar} />

      <div ref={seccionFormulario} className="mt-5">
        <h2 className="mb-3">{usuarioEditando ? `Editar usuario: ${nombreTitulo}` : "Crear usuario"}</h2>
        <FormularioUsuario
          key={`${claveFormulario}-${versionFormulario}`}
          tiposPermitidos={["Administrador", "Vendedor", "Cliente"]}
          textoBoton="Guardar usuario"
          mostrarLimpiar
          usuario={claveFormulario === "nuevo" ? null : usuarioEditando}
          bloquearRun={runEditando !== null}
          claveObligatoria={runEditando === null}
          onGuardar={guardar}
          onLimpiar={prepararNuevo}
        />
      </div>
    </div>
  );
}

export default AdminUsuarios;
