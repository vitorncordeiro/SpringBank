package br.pucpr.banco.integracaobc;

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
