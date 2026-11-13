package br.pucpr.banco.pagamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PixRequest(@NotNull Long contaOrigemId, @NotNull @Positive BigDecimal valor,
                        @NotBlank String codigoQr) {
}
