package br.pucpr.banco.autenticacao.dto;

import br.pucpr.banco.autenticacao.model.Usuario;

public record UsuarioResponse(Long id, String nome, String cpf, boolean ativo) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getCpf(), u.isAtivo());
    }
}
