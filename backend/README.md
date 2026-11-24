# Sonido Vivo API

Siete microservicios Spring Boot. Cada uno tiene su base, sus migraciones Flyway y las carpetas `controller`, `service` y `repository`. El navegador habla con uno solo: Vite reparte `/api`.

| Servicio | Carpeta | Puerto | Qué guarda |
| --- | --- | --- | --- |
| Catálogo | `catalogo` | 8081 | productos |
| Auth | `auth` | 8082 | tokens de sesión |
| Usuarios | `usuarios` | 8084 | cuentas y claves |
| Carrito | `carrito` | 8083 | líneas del carrito |
| Pedidos | `pedidos` | 8085 | pedidos confirmados |
| Pagos | `pagos` | 8086 | medio de pago simulado |
| Contacto | `contacto` | 8087 | mensajes del formulario |

Auth pregunta a usuarios si el correo y la clave son correctos, y después firma el JWT. Pagos lee el carrito, crea el pedido y guarda el medio elegido. No hay un cargo real.

## Arrancar

Hace falta JDK 25 o superior y Maven 3.9 o superior. Desde esta carpeta, en siete terminales:

```bash
mvn -pl catalogo spring-boot:run
mvn -pl auth spring-boot:run
mvn -pl usuarios spring-boot:run
mvn -pl carrito spring-boot:run
mvn -pl pedidos spring-boot:run
mvn -pl pagos spring-boot:run
mvn -pl contacto spring-boot:run
```

En Windows, `iniciar.ps1` abre las siete ventanas.

Swagger: `http://localhost:PUERTO/swagger-ui/index.html`. Consola H2: `http://localhost:PUERTO/h2-console`, usuario `sa`, contraseña vacía. JDBC: `jdbc:h2:file:./data/catalogo` (o `auth`, `usuarios`, `carrito`, `pedidos`, `pagos`, `contacto`).

La base por defecto es H2. Flyway crea las tablas y carga 51 productos y tres cuentas. Hibernate no modifica el esquema (`ddl-auto=validate`).

## Cuentas

| Correo | Clave | Rol |
| --- | --- | --- |
| juan@gmail.com | juan1234 | Administrador |
| laura@gmail.com | laura1234 | Vendedor |
| pedro@gmail.com | pedro1234 | Cliente |

## MySQL con Laragon

```bash
mvn -pl auth spring-boot:run -Dspring-boot.run.profiles=mysql
```

El mismo perfil en los otros seis. Se crean `sonidovivo_auth`, `sonidovivo_usuarios`, `sonidovivo_catalogo`, `sonidovivo_carrito`, `sonidovivo_pedidos`, `sonidovivo_pagos` y `sonidovivo_contacto`. Usuario `root`, clave `root`.

## Pruebas

```bash
mvn test
```

## Frontend

En la carpeta React: `npm install` la primera vez y `npm run dev`.
