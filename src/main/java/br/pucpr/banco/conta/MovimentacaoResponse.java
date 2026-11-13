package br.pucpr.banco.conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoResponse(Long id, TipoMovimentacao tipo, BigDecimal valor, BigDecimal saldoApos,
                                   String referencia, LocalDateTime criadaEm) {
    static MovimentacaoResponse de(Movimentacao m) {
        return new MovimentacaoResponse(m.getId(), m.getTipo(), m.getValor(), m.getSaldoApos(),
                m.getReferencia(), m.getCriadaEm());
    }
}
