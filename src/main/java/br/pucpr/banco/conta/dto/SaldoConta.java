package br.pucpr.banco.conta.dto;

import br.pucpr.banco.conta.model.Conta;

import java.math.BigDecimal;

public record SaldoConta(Long contaId, BigDecimal saldo, BigDecimal saldoReservado, BigDecimal disponivel) {
    public static SaldoConta de(Conta c) {
        return new SaldoConta(c.getId(), c.getSaldo(), c.getSaldoReservado(), c.getSaldoDisponivel());
    }
}
