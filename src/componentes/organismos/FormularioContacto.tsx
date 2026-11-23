import { useState, type ChangeEvent, type FormEvent } from "react";
import CampoFormulario from "../moleculas/CampoFormulario";
import CampoTextArea from "../moleculas/CampoTextArea";
import Boton from "../atomos/Boton";
import { validarContacto, hayErrores, type DatosContacto, type Errores } from "../../utilidades/validaciones";
import { enviarContacto } from "../../servicios/api";

const DATOS_VACIOS: DatosContacto = { nombre: "", correo: "", comentario: "" };

function FormularioContacto() {
  const [datos, setDatos] = useState<DatosContacto>(DATOS_VACIOS);
  const [errores, setErrores] = useState<Errores<DatosContacto>>({});
  const [exito, setExito] = useState(false);

  const manejarCambio = (evento: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = evento.target;
    setDatos({ ...datos, [name]: value });
    setExito(false);
  };

  const manejarEnvio = async (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault();
    const nuevosErrores = validarContacto(datos);
    setErrores(nuevosErrores);
    if (hayErrores(nuevosErrores)) {
      setExito(false);
      return;
    }
    try {
      await enviarContacto({
        nombre: datos.nombre.trim(),
        correo: datos.correo.trim(),
        comentario: datos.comentario.trim(),
      });
      setExito(true);
      setDatos(DATOS_VACIOS);
    } catch (error) {
      setExito(false);
      setErrores({ comentario: error instanceof Error ? error.message : "No se pudo enviar el mensaje." });
    }
  };

  return (
    <form onSubmit={manejarEnvio} noValidate>
      {exito && (
        <div className="alert alert-success" role="alert">
          ¡Mensaje enviado! Te responderemos pronto.
        </div>
      )}

      <CampoFormulario
        id="contacto-nombre"
        etiqueta="Nombre"
        nombre="nombre"
        valor={datos.nombre}
        onChange={manejarCambio}
        error={errores.nombre}
        placeholder="Tu nombre"
        maxLength={100}
      />

      <CampoFormulario
        id="contacto-correo"
        etiqueta="Correo"
        nombre="correo"
        tipo="email"
        valor={datos.correo}
        onChange={manejarCambio}
        error={errores.correo}
        placeholder="tu@correo.cl"
        maxLength={100}
      />

      <CampoTextArea
        id="contacto-comentario"
        etiqueta="Comentario"
        nombre="comentario"
        valor={datos.comentario}
        onChange={manejarCambio}
        error={errores.comentario}
        placeholder="Escribe tu mensaje (máx. 500 caracteres)"
        maxLength={500}
      />

      <Boton type="submit">Enviar</Boton>
    </form>
  );
}

export default FormularioContacto;
