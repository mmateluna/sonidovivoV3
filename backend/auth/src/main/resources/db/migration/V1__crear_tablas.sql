-- Solo los refresh tokens. La tabla usuario está en el servicio de usuarios.
create table token_refresco (
  id bigint not null auto_increment primary key,
  valor varchar(700) not null,
  usuario_id bigint not null,
  correo varchar(100) not null,
  expira timestamp not null,
  constraint uk_token_valor unique (valor)
);
