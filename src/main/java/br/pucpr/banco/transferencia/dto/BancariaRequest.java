package br.pucpr.banco.transferencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BancariaRequest(@NotNull Long contaOrigemId, @NotNull @Positive BigDecimal valor,
                             @NotBlank String banco, @NotBlank String agencia, @NotBlank String conta,
                             @NotBlank String favorecido) {
}
