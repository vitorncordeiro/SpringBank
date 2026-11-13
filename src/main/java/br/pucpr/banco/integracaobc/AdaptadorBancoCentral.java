package br.pucpr.banco.integracaobc;

public interface AdaptadorBancoCentral {
    OperacaoBc operacao();

    String enviar(String requisicao);
}
