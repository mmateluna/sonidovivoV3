-- Carrito y pedido no referencian al usuario ni al producto: viven en otras bases.
create table carrito_item (
  id bigint not null auto_increment primary key,
  usuario_id bigint not null,
  producto_id bigint not null,
  nombre varchar(100) not null,
  precio int not null,
  imagen varchar(300),
  cantidad int not null,
  stock int not null,
  constraint uk_carrito_usuario_producto unique (usuario_id, producto_id)
);

create table pedido (
  id bigint not null auto_increment primary key,
  usuario_id bigint not null,
  metodo varchar(40) not null,
  total int not null,
  creado timestamp not null
);

create table pedido_item (
  id bigint not null auto_increment primary key,
  pedido_id bigint not null,
  producto_id bigint not null,
  nombre varchar(100) not null,
  precio int not null,
  cantidad int not null,
  constraint fk_item_pedido foreign key (pedido_id) references pedido (id)
);
