import { Link } from "react-router-dom";
import BotonLink from "../componentes/atomos/BotonLink";
import FormularioLogin from "../componentes/organismos/FormularioLogin";
import { useAuth } from "../contexto/AuthContext";

function Login() {
  const { usuario, listo } = useAuth();
  const puedeAdministrar = usuario?.tipo === "Administrador" || usuario?.tipo === "Vendedor";

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-6">
          <h2>{usuario ? "Sesión iniciada" : "Iniciar sesión"}</h2>
          {!usuario && listo && <p className="mb-4">Ingresa con tu correo y contraseña.</p>}
          {listo ? <FormularioLogin /> : <p>Cargando sesión...</p>}

          {usuario ? (
            <div className="d-flex flex-wrap gap-2">
              <BotonLink to="/catalogo">Ir al catálogo</BotonLink>
              {puedeAdministrar && (
                <BotonLink to="/admin" variante="secondary">
                  Ir a administración
                </BotonLink>
              )}
            </div>
          ) : (
            listo && (
              <>
                <p className="text-muted small">
                  Cuentas de prueba: juan@gmail.com / juan1234 (administrador), laura@gmail.com / laura1234 (vendedor),
                  pedro@gmail.com / pedro1234 (cliente).
                </p>
                <p className="mt-3">
                  ¿No tienes cuenta? <Link to="/registro">Regístrate aquí</Link>
                </p>
              </>
            )
          )}
        </div>
      </div>
    </div>
  );
}

export default Login;
