# Sonido Vivo API

Tres microservicios Spring Boot. Cada uno tiene su base y sus migraciones Flyway. El navegador habla con uno solo: Vite reparte `/api`.

| Servicio | Carpeta | Puerto | Qué guarda |
| --- | --- | --- | --- |
| Catálogo | `catalogo` | 8081 | productos |
| Cuentas | `cuentas` | 8082 | usuarios y tokens |
| Carrito | `carrito` | 8083 | carrito y pedidos |

El panel de administración no es un cuarto servicio. El administrador y el vendedor llaman al catálogo para los productos y a cuentas para los usuarios.

## Arrancar

Hace falta JDK 17 o superior y Maven 3.9 o superior. Desde esta carpeta, en tres terminales:

```bash
mvn -pl catalogo spring-boot:run
mvn -pl cuentas spring-boot:run
mvn -pl carrito spring-boot:run
```

En Windows, `iniciar.ps1` abre las tres ventanas.

- Catálogo: http://localhost:8081/swagger-ui/index.html
- Cuentas: http://localhost:8082/swagger-ui/index.html
- Carrito: http://localhost:8083/swagger-ui/index.html
- Consola H2 de cada uno: `http://localhost:PUERTO/h2-console`
  - JDBC: `jdbc:h2:file:./data/catalogo` (o `cuentas`, o `carrito`)
  - Usuario `sa`, contraseña vacía

La base por defecto es H2, para poder demostrar sin instalar MySQL. Flyway crea las tablas y carga 51 productos y tres cuentas. Hibernate no modifica el esquema (`ddl-auto=validate`).

## Cuentas

| Correo | Clave | Rol |
| --- | --- | --- |
| juan@gmail.com | juan1234 | Administrador |
| laura@gmail.com | laura1234 | Vendedor |
| pedro@gmail.com | pedro1234 | Cliente |

## MySQL con Laragon

Laragon es el MySQL de la clase. Ábrelo, deja MySQL en marcha y arranca cada servicio con el perfil `mysql`:

```bash
mvn -pl cuentas spring-boot:run -Dspring-boot.run.profiles=mysql
```

Igual para `catalogo` y `carrito`. Se crean las bases `sonidovivo_cuentas`, `sonidovivo_catalogo` y `sonidovivo_carrito`. Usuario `root`, clave `root`. Si Laragon usa otra clave, cámbiala en `application-mysql.properties` de cada servicio. Las mismas migraciones de `db/migration` corren en MySQL.

## Pruebas

```bash
mvn test
```

## Frontend

En la carpeta React: `npm install` la primera vez y `npm run dev`.
