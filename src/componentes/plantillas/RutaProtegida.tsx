import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../contexto/AuthContext";
import type { TipoUsuario } from "../../tipos";

interface RutaProtegidaProps {
  roles: TipoUsuario[];
  // A dónde va alguien que inició sesión pero no tiene el rol.
  destino?: string;
}

// Si no hay sesión, manda al login. Si el rol no alcanza, manda a destino.
function RutaProtegida({ roles, destino = "/" }: RutaProtegidaProps) {
  const { usuario, listo } = useAuth();

  if (!listo) {
    return <p className="container py-4">Cargando sesión...</p>;
  }
  if (!usuario) {
    return <Navigate to="/login" replace />;
  }
  if (!roles.includes(usuario.tipo)) {
    return <Navigate to={destino} replace />;
  }
  return <Outlet />;
}

export default RutaProtegida;
