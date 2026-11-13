package br.pucpr.banco.conta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AbrirContaRequest(@NotNull Long usuarioId, @NotBlank String agencia, @NotBlank String numero) {
}
