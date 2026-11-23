-- Cuentas de la demo. Las claves son el hash BCrypt de juan1234, laura1234 y pedro1234.
insert into usuario (run, nombre, apellidos, correo, clave, rol, region, comuna, direccion, fecha_nacimiento) values
('123456785', 'Juan', 'González', 'juan@gmail.com', '$2a$10$gujINuPS0FXMNRDNsftQY.aD3O9skrCfcl8xQklhO8RlVaCVJrkUq', 'ADMINISTRADOR', 'Región de Valparaíso', 'Viña del Mar', 'Calle 1', ''),
('987654325', 'Laura', 'Díaz', 'laura@gmail.com', '$2a$10$RDunQywtvHG9uLw3Ac7TROO7bFbFXPLbo/DGpAbE.UTw3pQoFMFAa', 'VENDEDOR', 'Región de Valparaíso', 'Valparaíso', 'Calle 2', ''),
('112223339', 'Pedro', 'Rojas', 'pedro@gmail.com', '$2a$10$l6qJdZlJMhf/9KtglXTGRe3bnmQO2a3Z/VTMvrQ7hs/8lqAHHxiWy', 'CLIENTE', 'Región Metropolitana', 'Santiago', 'Calle 3', '');
