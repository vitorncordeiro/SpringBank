package br.pucpr.banco.integracaobc;

import br.pucpr.banco.integracaobc.enums.OperacaoBc;

public interface AdaptadorBancoCentral {
    OperacaoBc operacao();

    String enviar(String requisicao);
}
