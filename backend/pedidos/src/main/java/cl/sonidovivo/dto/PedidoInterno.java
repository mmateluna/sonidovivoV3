package cl.sonidovivo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoInterno(
    @Min(1) long usuarioId,
    @NotEmpty @Valid List<LineaPedido> lineas
) {}
