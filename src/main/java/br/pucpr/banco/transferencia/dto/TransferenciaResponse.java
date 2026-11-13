package br.pucpr.banco.transferencia.dto;

import br.pucpr.banco.transferencia.model.Transferencia;
import br.pucpr.banco.transferencia.enums.StatusTransferencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaResponse(Long id, String tipo, Long contaOrigemId, BigDecimal valor,
                                   StatusTransferencia status, String comprovante, String motivoFalha,
                                   LocalDateTime criadaEm, LocalDateTime atualizadaEm) {
    public static TransferenciaResponse de(Transferencia t) {
        return new TransferenciaResponse(t.getId(), t.getTipo(), t.getContaOrigemId(), t.getValor(),
                t.getStatus(), t.getProtocoloBc(), t.getMotivoFalha(), t.getCriadaEm(), t.getAtualizadaEm());
    }
}
