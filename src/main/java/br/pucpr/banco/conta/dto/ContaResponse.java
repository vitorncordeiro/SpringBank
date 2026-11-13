package br.pucpr.banco.conta.dto;

import br.pucpr.banco.conta.model.Conta;

import java.math.BigDecimal;

public record ContaResponse(Long id, Long usuarioId, String agencia, String numero,
                           BigDecimal saldo, BigDecimal saldoReservado, BigDecimal saldoDisponivel) {
    public static ContaResponse de(Conta c) {
        return new ContaResponse(c.getId(), c.getUsuarioId(), c.getAgencia(), c.getNumero(),
                c.getSaldo(), c.getSaldoReservado(), c.getSaldoDisponivel());
    }
}
