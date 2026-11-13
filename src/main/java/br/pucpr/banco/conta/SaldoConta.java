package br.pucpr.banco.conta;

import java.math.BigDecimal;

public record SaldoConta(Long contaId, BigDecimal saldo, BigDecimal saldoReservado, BigDecimal disponivel) {
    static SaldoConta de(Conta c) {
        return new SaldoConta(c.getId(), c.getSaldo(), c.getSaldoReservado(), c.getSaldoDisponivel());
    }
}
