package br.pucpr.banco.integracaobc.dto;

import br.pucpr.banco.integracaobc.AdaptadorBancoCentral;
import br.pucpr.banco.integracaobc.enums.OperacaoBc;

import java.util.function.Function;

public record AdaptadorSimulado(OperacaoBc operacao, Function<String, String> acao) implements AdaptadorBancoCentral {
    @Override
    public String enviar(String requisicao) {
        if (requisicao.contains("FALHAR")) {
            throw new IllegalStateException("Banco Central indisponível (simulado)");
        }
        return acao.apply(requisicao);
    }
}
