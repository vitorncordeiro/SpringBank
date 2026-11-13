package br.pucpr.banco.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

public record NovoUsuarioRequest(@NotBlank String nome, @NotBlank String cpf, @NotBlank String senha) {
}
