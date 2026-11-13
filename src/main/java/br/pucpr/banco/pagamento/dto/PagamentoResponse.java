package br.pucpr.banco.pagamento.dto;

import br.pucpr.banco.pagamento.model.Pagamento;
import br.pucpr.banco.pagamento.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(Long id, String tipo, Long contaOrigemId, BigDecimal valor,
                               StatusPagamento status, String comprovante, String motivoFalha,
                               LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
    public static PagamentoResponse de(Pagamento p) {
        return new PagamentoResponse(p.getId(), p.getTipo(), p.getContaOrigemId(), p.getValor(),
                p.getStatus(), p.getProtocoloBc(), p.getMotivoFalha(), p.getCriadoEm(), p.getAtualizadoEm());
    }
}
