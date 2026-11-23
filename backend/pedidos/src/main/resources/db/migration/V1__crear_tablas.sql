-- Pedido e ítems. No hay llave foránea hacia el usuario ni hacia el producto.
create table pedido (
  id bigint not null auto_increment primary key,
  usuario_id bigint not null,
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
