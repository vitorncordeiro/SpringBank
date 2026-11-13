package br.pucpr.banco.pagamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BoletoRequest(@NotNull Long contaOrigemId, @NotBlank String codigoBarras) {
}
