package br.pucpr.banco.integracaobc.dto;

import br.pucpr.banco.integracaobc.model.MensagemBancoCentral;
import br.pucpr.banco.integracaobc.enums.OperacaoBc;
import br.pucpr.banco.integracaobc.enums.StatusMensagemBc;

import java.time.LocalDateTime;

public record MensagemResponse(Long id, String chaveIdempotencia, OperacaoBc operacao, StatusMensagemBc status,
                              String requisicao, String resposta, LocalDateTime criadaEm) {
    public static MensagemResponse de(MensagemBancoCentral m) {
        return new MensagemResponse(m.getId(), m.getChaveIdempotencia(), m.getOperacao(), m.getStatus(),
                m.getRequisicao(), m.getResposta(), m.getCriadaEm());
    }
}
