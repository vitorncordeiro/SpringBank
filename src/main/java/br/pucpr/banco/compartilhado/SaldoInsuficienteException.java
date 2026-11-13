package br.pucpr.banco.compartilhado;

/** Regra de negócio dos diagramas de sequência: [saldo < valor] -> 422 Saldo insuficiente. */
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
